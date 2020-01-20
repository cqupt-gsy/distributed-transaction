package apprentice.practice.transferin.services;

import static apprentice.practice.api.enums.Results.TRANSFER_FAILED;
import static apprentice.practice.api.enums.Results.TRANSFER_SUCCESS;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import apprentice.practice.api.transferin.command.TransferInCommand;
import apprentice.practice.api.transferin.services.TransferInService;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.Service;

@Service
public class TransferInServiceImpl implements TransferInService {

  @Override
  public Results tryTransferIn(TransferInCommand command) {
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    // 先获得锁，在操作，可以保障顺序执行，CONFIRM和CANCEL阶段不会出现问题，但是并发性降低了很多
    // TODO 可以先在本地操作，最后再去获取锁，这样就需要在CONFIRM和CANCEL阶段，如果数据被其他的交易修改了怎么办？
    distributedLockService.tryLock(userId, transactionNumber);

    // 因为有全局分布式锁，所以可以保障，查出来的用户数据就是最新的
    Account account = accountRepository.findAccountBy(userId);
    if (account == null) {
      log.info(
          "Try transfer in failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_FAILED;
    }
    BigDecimal originalBalance = account.getBalance();
    BigDecimal newBalance = originalBalance.add(transactionMoney);
    accountRepository.saveAccountBackUp(
        AccountBackUp.createBy(
            userId, transactionNumber, originalBalance, newBalance, transactionMoney));
    if (accountRepository.transferMoney(userId, newBalance)) {
      log.info(
          "Try transfer in success from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_SUCCESS;
    } else {
      log.info(
          "Try transfer in failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_FAILED; // 余额不足不会发生变化，可以进入CANCEL状态
    }
  }

  @Override
  public void confirmTransferIn(TransferInCommand command) {
    Integer userId = transferOutCommand.getUserId();
    String transactionNumber = transferOutCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferOutCommand.getBalance();
    accountRepository.updateAccountBackUp(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Confirm transfer in success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }

  @Override
  public void cancelTransferIn(TransferInCommand command) {
    Integer userId = transferOutCommand.getUserId();
    String transactionNumber = transferOutCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferOutCommand.getBalance();
    AccountBackUp accountBackUp = accountRepository.findAccountBackUp(userId, transactionNumber);
    accountRepository.transferMoney(userId, accountBackUp.getOriginalBalance());
    accountRepository.updateAccountBackUp(userId, transactionNumber, CANCEL);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Cancle transfer in success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }
}
