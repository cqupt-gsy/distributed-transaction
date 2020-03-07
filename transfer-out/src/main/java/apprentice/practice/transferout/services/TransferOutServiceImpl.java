package apprentice.practice.transferout.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.enums.Results.TRY_SUCCESS;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;
import static apprentice.practice.api.enums.Status.TRY;

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
  public Results tryTransferOut(TransferOutCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    distributedLockService.tryLock(userId, transactionNumber);
    AccountBackUp rollback = transferOutRepository.findRollback(userId, transactionNumber);

    if (rollback == null) {
      Account account = transferOutRepository.findAccount(userId);
      if (account == null) {
        logTryTransferStatus(userId, transactionNumber, transactionMoney, TRY_FAILED.name());
        return TRY_FAILED;
      }
      BigDecimal originalBalance = account.getBalance();
      BigDecimal newBalance = originalBalance.subtract(transactionMoney);
      if (newBalance.doubleValue() < 0) {
        return TRY_FAILED;
      }
      transferOutRepository.saveRollback(
          AccountBackUp.createBy(
              userId, transactionNumber, originalBalance, newBalance, transactionMoney));
      if (transferOutRepository.transfer(userId, newBalance)) {
        logTryTransferStatus(userId, transactionNumber, transactionMoney, TRY_SUCCESS.name());
        return TRY_SUCCESS;
      } else {
        logTryTransferStatus(userId, transactionNumber, transactionMoney, TRY_FAILED.name());
        return TRY_FAILED;
      }
    }

    if (rollback.getStatus() == CONFIRM || rollback.getStatus() == TRY) {
      return TRY_SUCCESS;
    } else {
      return TRY_FAILED;
    }
  }

  @Override
  @Transactional
  public void confirmTransferOut(TransferOutCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    distributedLockService.unLock(userId, transactionNumber);

    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp rollback = transferOutRepository.findRollback(userId, transactionNumber);
    if (rollback == null || rollback.getStatus() == CONFIRM) {
      return;
    }
    transferOutRepository.updateRollback(userId, transactionNumber, CONFIRM);
    logConfirmOrCancelStatus(userId, transactionNumber, transactionMoney, CONFIRM.name());
  }

  @Override
  @Transactional
  public void cancelTransferOut(TransferOutCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    distributedLockService.unLock(userId, transactionNumber);

    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp rollback = transferOutRepository.findRollback(userId, transactionNumber);
    if (rollback == null || rollback.getStatus() == CANCEL) {
      return;
    }
    transferOutRepository.transfer(userId, rollback.getOriginalBalance());
    transferOutRepository.updateRollback(userId, transactionNumber, CANCEL);
    logConfirmOrCancelStatus(userId, transactionNumber, transactionMoney, CANCEL.name());
  }

  private void logTryTransferStatus(
      Integer userId, String transactionNumber, BigDecimal transactionMoney, String status) {
    log.info(
        "Transfer out {} from {} with {} for {}",
        status,
        userId,
        transactionMoney,
        transactionNumber);
  }

  private void logConfirmOrCancelStatus(
      Integer userId, String transactionNumber, BigDecimal transactionMoney, String status) {
    log.info(
        "Transfer out {} from {} with {} for {}",
        status,
        userId,
        transactionMoney,
        transactionNumber);
  }
}
