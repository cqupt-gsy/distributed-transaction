package apprentice.practice.transactions.services;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import apprentice.practice.transactions.TransactionRepository;
import apprentice.practice.transactions.command.TransactionCommand;
import apprentice.practice.transactions.model.Transaction;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private final TransactionRepository repository;

  @Autowired
  public TransactionService(TransactionRepository repository) {
    this.repository = repository;
  }

  public List<Transaction> findAll() {
    return repository.findAll();
  }

  public void begin(TransactionCommand command) {
    verifyTransactionMoney(command.getTransactionMoney());
    verifyAccount(command.getTransformerAccount(), command.getTransformerName());
    verifyAccount(command.getTransformeeAccount(), command.getTransformeeName());
    verifyEnvelope(command.getEnvelopeId(), command.getEnvelopeMoney());
    verifyIntegral(command.getIntegralId(), command.getIntegral());
    verifyTransactionNumber(command.getTransactionNumber());

    Transaction transaction = Transaction.createBy(command);
    repository.save(transaction);
  }

  private void verifyTransactionMoney(BigDecimal transactionMoney) {
    if (isNull(transactionMoney) || transactionMoney.doubleValue() < 0) {
      throw new InvalidParameterException("Transaction money invalid");
    }
  }

  private void verifyAccount(String account, String name) {
    if (isBlank(account) || isBlank(name)) {
      throw new InvalidParameterException("Transaction account invalid");
    }
  }

  private void verifyEnvelope(String envelopeId, BigDecimal envelopeMoney) {
    if (isBlank(envelopeId) && nonNull(envelopeMoney)) {
      throw new InvalidParameterException("Transaction envelope invalid");
    }
    if (isNotBlank(envelopeId) && isNull(envelopeMoney)) {
      throw new InvalidParameterException("Transaction envelope invalid");
    }
    if (isNotBlank(envelopeId) && nonNull(envelopeMoney) && envelopeMoney.doubleValue() < 0) {
      throw new InvalidParameterException("Transaction envelope invalid");
    }
  }

  private void verifyIntegral(String integralId, Integer integral) {
    if (isBlank(integralId) && nonNull(integral)) {
      throw new InvalidParameterException("Transaction integral invalid");
    }
    if (isNotBlank(integralId) && isNull(integral)) {
      throw new InvalidParameterException("Transaction integral invalid");
    }
    if (isNotBlank(integralId) && nonNull(integral) && integral < 0) {
      throw new InvalidParameterException("Transaction integral invalid");
    }
  }

  private void verifyTransactionNumber(String transactionNumber) {
    if (repository.existBy(transactionNumber)) {
      throw new InvalidParameterException("Transaction is already started, please wait a minute...");
    }
  }
}
