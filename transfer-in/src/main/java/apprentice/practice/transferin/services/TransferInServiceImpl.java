package apprentice.practice.transferin.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.enums.Results.TRY_SUCCESS;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;
import static apprentice.practice.api.enums.Status.TRY;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import apprentice.practice.api.transferin.command.TransferInCommand;
import apprentice.practice.api.transferin.services.TransferInService;
import apprentice.practice.transferin.repositories.TransferInRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransferInServiceImpl implements TransferInService {

  private final TransferInRepository transferInRepository;
  private final DistributedLockService distributedLockService;

  @Autowired
  public TransferInServiceImpl(
      TransferInRepository transferInRepository, DistributedLockService distributedLockService) {
    this.transferInRepository = transferInRepository;
    this.distributedLockService = distributedLockService;
  }

  @Override
  @Transactional
  public Results tryTransferIn(TransferInCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();

    distributedLockService.tryLock(userId, transactionNumber);
    AccountBackUp rollback = transferInRepository.findRollback(userId, transactionNumber);

    if (rollback == null) {
      Account account = transferInRepository.findAccount(userId);
      if (account == null) {
        logTryTransferStatus(userId, transactionNumber, transactionMoney, TRY_FAILED.name());
        return TRY_FAILED;
      }
      BigDecimal originalBalance = account.getBalance();
      BigDecimal newBalance = originalBalance.add(transactionMoney);
      transferInRepository.saveRollback(
          AccountBackUp.createBy(
              userId, transactionNumber, originalBalance, newBalance, transactionMoney));
      if (transferInRepository.transfer(userId, newBalance)) {
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
  public void confirmTransferIn(TransferInCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp rollback = transferInRepository.findRollback(userId, transactionNumber);
    if (rollback.getStatus() == CONFIRM) {
      return;
    }

    transferInRepository.updateRollback(userId, transactionNumber, CONFIRM);
    distributedLockService.unLock(userId, transactionNumber);
    logConfirmOrCancelStatus(userId, transactionNumber, transactionMoney, CONFIRM.name());
  }

  @Override
  @Transactional
  public void cancelTransferIn(TransferInCommand command) {  // 允许幂等操作
    Integer userId = command.getUserId();
    String transactionNumber = command.getTransactionNumber();
    BigDecimal transactionMoney = command.getBalance();
    AccountBackUp rollback = transferInRepository.findRollback(userId, transactionNumber);
    if (rollback.getStatus() == CANCEL) {
      return;
    }

    transferInRepository.transfer(userId, rollback.getOriginalBalance());
    transferInRepository.updateRollback(userId, transactionNumber, CANCEL);
    distributedLockService.unLock(userId, transactionNumber);
    logConfirmOrCancelStatus(userId, transactionNumber, transactionMoney, CANCEL.name());
  }

  private void logTryTransferStatus(
      Integer userId, String transactionNumber, BigDecimal transactionMoney, String status) {
    log.info(
        "Transfer in {} from {} with {} for {}",
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
