package apprentice.practice.accounts.services;

import static apprentice.practice.api.services.enums.Results.TRANSFER_FAILED;
import static apprentice.practice.api.services.enums.Results.TRANSFER_SUCCESS;
import static apprentice.practice.api.services.enums.Status.CANCEL;
import static apprentice.practice.api.services.enums.Status.CONFIRM;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.model.Account;
import apprentice.practice.accounts.model.AccountBackUp;
import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.CreateAccountCommand;
import apprentice.practice.api.services.command.TransferCommand;
import apprentice.practice.api.services.enums.Results;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final DistributedLockService distributedLockService;

  @Autowired
  public AccountServiceImpl(
      AccountRepository accountRepository, DistributedLockService distributedLockService) {
    this.accountRepository = accountRepository;
    this.distributedLockService = distributedLockService;
  }

  @Override
  public void create(CreateAccountCommand command) {
    Account account = Account.createBy(command);
    accountRepository.saveAccount(account);
  }

  @Override
  @Transactional
  // 该方法不支持重试，因为重试会报异常，导致事务管理器决定CANCEL，则会把以前的状态冲掉
  public Results tryTransferFrom(TransferCommand command) {
    Integer userId = command.getTransferId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    // 先获得锁，在操作，可以保障顺序执行，CONFIRM和CANCEL阶段不会出现问题，但是并发性降低了很多
    // TODO 可以先在本地操作，最后再去获取锁，这样就需要在CONFIRM和CANCEL阶段，如果数据被其他的交易修改了怎么办？
    distributedLockService.tryLock(userId, transactionNumber);

    // 因为有全局分布式锁，所以可以保障，查出来的用户数据就是最新的
    Account account = accountRepository.findAccountBy(userId);
    if (account == null) {
      log.info(
          "Try transfer out failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_FAILED;
    }
    BigDecimal originalBalance = account.getBalance();
    BigDecimal newBalance = originalBalance.subtract(transactionMoney);
    accountRepository.saveAccountBackUp(
        AccountBackUp.createBy(
            userId, transactionNumber, originalBalance, newBalance, transactionMoney));
    if (accountRepository.transferMoney(userId, newBalance)) {
      log.info(
          "Try transfer out success from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_SUCCESS;
    } else {
      log.info(
          "Try transfer out failed from {} with {} for {}",
          userId,
          transactionMoney,
          transactionNumber);
      return TRANSFER_FAILED; // 余额不足不会发生变化，可以进入CANCEL状态
    }
  }

  @Override
  @Transactional
  public void confirmTransferFrom(TransferCommand transferCommand) {
    Integer userId = transferCommand.getTransferId();
    String transactionNumber = transferCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferCommand.getBalance();
    accountRepository.updateAccountBackUp(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Confirm transfer out success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }

  @Override
  @Transactional
  public void cancelTransferFrom(TransferCommand transferCommand) {
    Integer userId = transferCommand.getTransferId();
    String transactionNumber = transferCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferCommand.getBalance();
    AccountBackUp accountBackUp = accountRepository.findAccountBackUp(userId, transactionNumber);
    accountRepository.transferMoney(userId, accountBackUp.getOriginalBalance());
    accountRepository.updateAccountBackUp(userId, transactionNumber, CANCEL);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Cancel transfer out success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }


  @Override
  @Transactional
  public Results tryTransferTo(TransferCommand command) {
    Integer userId = command.getTransferId();
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
  @Transactional
  public void confirmTransferTo(TransferCommand transferCommand) {
    Integer userId = transferCommand.getTransferId();
    String transactionNumber = transferCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferCommand.getBalance();
    accountRepository.updateAccountBackUp(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    log.info(
        "Confirm transfer in success from {} with {} for {}",
        userId,
        transactionMoney,
        transactionNumber);
  }

  @Override
  @Transactional
  public void cancelTransferTo(TransferCommand transferCommand) {
    Integer userId = transferCommand.getTransferId();
    String transactionNumber = transferCommand.getTransactionNumber();
    BigDecimal transactionMoney = transferCommand.getBalance();
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
