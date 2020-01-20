package apprentice.practice.transferin.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.enums.Results.TRY_SUCCESS;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import apprentice.practice.api.transferin.command.TransferInCommand;
import apprentice.practice.api.transferin.services.TransferInService;
import apprentice.practice.transferin.repositories.TransferInRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

@Service
@Slf4j
public class TransferInServiceImpl implements TransferInService {

  private final TransferInRepository transferInRepository;
  private final DistributedLockService distributedLockService;

  public TransferInServiceImpl(
      TransferInRepository transferInRepository, DistributedLockService distributedLockService) {
    this.transferInRepository = transferInRepository;
    this.distributedLockService = distributedLockService;
  }

  @Override
  public Results tryTransferIn(TransferInCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    distributedLockService.tryLock(userId, transactionNumber);

    // 因为有全局分布式锁，所以可以保障，查出来的用户数据就是最新的
    Account account = transferInRepository.findAccount(userId);
    if (account == null) {
      log.info(
          "Try transfer in failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_FAILED;
    }
    BigDecimal originalBalance = account.getBalance();
    BigDecimal newBalance = originalBalance.add(transactionMoney);
    transferInRepository.saveRollback(
        AccountBackUp.createBy(
            userId, transactionNumber, originalBalance, newBalance, transactionMoney));
    if (transferInRepository.transfer(userId, newBalance)) {
      log.info(
          "Try transfer in success from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_SUCCESS;
    } else {
      log.info(
          "Try transfer in failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRY_FAILED; // 余额不足不会发生变化，可以进入CANCEL状态
    }
  }

  @Override
  public void confirmTransferIn(TransferInCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    transferInRepository.updateRollback(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Confirm transfer in success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }

  @Override
  public void cancelTransferIn(TransferInCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp accountBackUp = transferInRepository.findRollback(userId, transactionNumber);
    transferInRepository.transfer(userId, accountBackUp.getOriginalBalance());
    transferInRepository.updateRollback(userId, transactionNumber, CANCEL);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Cancel transfer in success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }
}
