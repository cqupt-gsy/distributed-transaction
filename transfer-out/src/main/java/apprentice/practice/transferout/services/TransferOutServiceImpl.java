package apprentice.practice.transferout.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.enums.Results.TRY_SUCCESS;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import apprentice.practice.api.transferout.command.TransferOutCommand;
import apprentice.practice.api.transferout.services.TransferOutService;
import apprentice.practice.transferout.repositories.TransferOutRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransferOutServiceImpl implements TransferOutService {

  private final TransferOutRepository transferOutRepository;
  private final DistributedLockService distributedLockService;

  @Autowired
  public TransferOutServiceImpl(
      TransferOutRepository transferOutRepository, DistributedLockService distributedLockService) {
    this.transferOutRepository = transferOutRepository;
    this.distributedLockService = distributedLockService;
  }

  @Override
  @Transactional
  public Results tryTransferOut(TransferOutCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    distributedLockService.tryLock(userId, transactionNumber);

    Account account = transferOutRepository.findAccount(userId);
    if (account == null) {
      log.info(
          "Try transfer out failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_FAILED;
    }
    BigDecimal originalBalance = account.getBalance();
    BigDecimal newBalance = originalBalance.subtract(transactionMoney);
    transferOutRepository.saveRollback(
        AccountBackUp.createBy(
            userId, transactionNumber, originalBalance, newBalance, transactionMoney));
    if (transferOutRepository.transfer(userId, newBalance)) {
      log.info(
          "Try transfer out success from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_SUCCESS;
    } else {
      log.info(
          "Try transfer out failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_FAILED;
    }
  }

  @Override
  @Transactional
  public void confirmTransferOut(TransferOutCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    transferOutRepository.updateRollback(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Confirm transfer out success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }

  @Override
  @Transactional
  public void cancelTransferOut(TransferOutCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp accountBackUp = transferOutRepository.findRollback(userId, transactionNumber);
    transferOutRepository.transfer(userId, accountBackUp.getOriginalBalance());
    transferOutRepository.updateRollback(userId, transactionNumber, CANCEL);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Cancel transfer out success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }
}
