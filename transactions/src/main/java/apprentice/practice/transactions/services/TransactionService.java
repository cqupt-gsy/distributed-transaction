package apprentice.practice.transactions.services;

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

  // 在这里可以保证事务开始时，一定先记录了交易记录，该记录可以保证以下两个故障
  // 1. 客户端发起重试时，可以保证交易的幂等
  // 2. 事务服务挂了后，可以利用该记录恢复事务
  @Transactional
  public boolean tryStartTransaction(TransactionCommand command) {
    Transaction transaction = Transaction.createBy(command);
    try {
      log.info(
          "Transactions begins with transaction number {}, transformerId {}, transformeeId {}",
          command.getTransactionNumber(),
          command.getTransformerId(),
          command.getTransformeeId());
      if (repository.existWithTryStatusFor(transaction.getTransactionNumber())) {
        log.info(
            "Transaction with transaction number {} already started in TRY status",
            command.getTransactionNumber());
        return true;
      }
      repository.save(transaction);
      return true;
    } catch (DuplicateKeyException ex) {
      log.error(
          "Transaction already exists with duplicated key {} ", transaction.getTransactionNumber());
      return false;
    }
  }


}
