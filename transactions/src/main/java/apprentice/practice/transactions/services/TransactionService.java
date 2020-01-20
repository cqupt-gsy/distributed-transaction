package apprentice.practice.transactions.services;

import static apprentice.practice.api.enums.Results.CANCEL_STATUS;
import static apprentice.practice.api.enums.Results.CONFIRM_STATUS;
import static apprentice.practice.api.enums.Results.CREATE_TRANSACTION_SUCCESS;
import static apprentice.practice.api.enums.Results.DUPLICATE_KEY;
import static apprentice.practice.api.enums.Results.TRYING_STATUS;
import static apprentice.practice.api.enums.Results.UNKNOWN_EXCEPTION;
import static apprentice.practice.api.enums.Status.CANCEL;
import static apprentice.practice.api.enums.Status.CONFIRM;
import static apprentice.practice.api.enums.Status.TRY;

import apprentice.practice.api.enums.Results;
import apprentice.practice.transactions.TransactionRepository;
import apprentice.practice.transactions.command.TransactionCommand;
import apprentice.practice.transactions.model.Transaction;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransactionService {

  private final TransactionRepository repository;

  @Autowired
  public TransactionService(TransactionRepository repository) {
    this.repository = repository;
  }

  public List<Transaction> findAll() {
    return repository.findAll();
  }

  // 该方法可以保证幂等，事务号相同的情况下，多次重试得到的结果是一致的
  // 需要特别注意TRY状态，为了保证本服务在挂了后，客户端可以真正的执行重试，所以需要执行跟SUCCESS一样的步骤
  // 因此所有的接口都必须幂等
  @Transactional
  public Results tryTransaction(TransactionCommand command) {
    String transactionNumber = command.getTransactionNumber();
    Transaction alreadyExistsTransaction = repository.selectTransaction(transactionNumber);
    if (alreadyExistsTransaction == null) { // 最初发生并发，都会进入该分支，但是会有一个失败，从而发起重试
      try {
        Transaction newTransaction = Transaction.createBy(command);
        repository.save(newTransaction);
        log.info("Start new transaction with transaction number {}", transactionNumber);
        return CREATE_TRANSACTION_SUCCESS;
      } catch (DuplicateKeyException ex) {
        log.error("Transaction for duplicated key {}, wait a minute for retry", transactionNumber);
        return DUPLICATE_KEY;
      }
    }
    if (alreadyExistsTransaction.getStatus() == CONFIRM) {
      log.warn("Transaction already in CONFIRM status for {}, do not retry", transactionNumber);
      return CONFIRM_STATUS;
    }
    if (alreadyExistsTransaction.getStatus() == CANCEL) {
      log.warn("Transaction already in CANCEL status for {}, need start again", transactionNumber);
      return CANCEL_STATUS;
    }
    if (alreadyExistsTransaction.getStatus() == TRY) {
      log.warn("Transaction in TRY status with {}, wait a minute for retry", transactionNumber);
      return TRYING_STATUS;
    }
    log.error("Transaction failed with unknown reason for {}, please try later", transactionNumber);
    return UNKNOWN_EXCEPTION;
  }

  @Transactional
  public boolean confirmTransaction(String transactionNumber) {
    log.info("Confirm transaction for transaction number {}", transactionNumber);
    return repository.update(transactionNumber, CONFIRM);
  }

  @Transactional
  public boolean cancelTransaction(String transactionNumber) {
    log.info("Cancel transaction for transaction number {}", transactionNumber);
    return repository.update(transactionNumber, CANCEL);
  }
}
