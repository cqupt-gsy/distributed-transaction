package apprentice.practice.transactions.services;

import static apprentice.practice.api.enums.Results.CANCEL_STATUS;
import static apprentice.practice.api.enums.Results.CONFIRM_STATUS;
import static apprentice.practice.api.enums.Results.CREATE_TRANSACTION_SUCCESS;
import static apprentice.practice.api.enums.Results.DUPLICATE_KEY;
import static apprentice.practice.api.enums.Results.TRANSACTION_FAILED;
import static apprentice.practice.api.enums.Results.TRANSACTION_SUCCESS;
import static apprentice.practice.api.enums.Results.TRYING_STATUS;
import static apprentice.practice.api.enums.Results.TRY_SUCCESS;
import static apprentice.practice.api.enums.Results.UNKNOWN_EXCEPTION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import apprentice.practice.api.enums.Results;
import apprentice.practice.transactions.command.TransactionCommand;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionManagerService {

  private final TransactionService transactionService;
  private final TransferOutServiceProxy transferOutServiceProxy;
  private final TransferInServiceProxy transferInServiceProxy;

  @Autowired
  public TransactionManagerService(
      TransactionService transactionService,
      TransferOutServiceProxy transferOutServiceProxy,
      TransferInServiceProxy transferInServiceProxy) {
    this.transactionService = transactionService;
    this.transferOutServiceProxy = transferOutServiceProxy;
    this.transferInServiceProxy = transferInServiceProxy;
  }

  // 资金数据正确排第一，其次考虑并发性
  public String execute(TransactionCommand command)
      throws ExecutionException, InterruptedException {
    verifyTransactionNumber(command.getTransactionNumber());
    verifyTransactionMoney(command.getTransactionMoney());
    verifyAccount(command.getTransformerId(), command.getTransformeeId());
    verifyEnvelope(command.getEnvelopeId(), command.getEnvelopeMoney());
    verifyIntegral(command.getIntegralId(), command.getIntegral());

    Results results = transactionService.tryTransaction(command);
    if (results == CONFIRM_STATUS
        || results == CANCEL_STATUS
        || results == DUPLICATE_KEY
        || results == UNKNOWN_EXCEPTION) {
      return results.getMessage();
    }

    if (results == CREATE_TRANSACTION_SUCCESS || results == TRYING_STATUS) {
      log.debug(results.getMessage());

      if (tryTransaction(command)) {
        // 底层服务采用集群部署，加上重试机制，肯定可以得到返回。这里保证必须释放锁，否则会对后续的交易产生影响
        confirmTransaction(command);
        transactionService.confirmTransaction(command.getTransactionNumber());
        return TRANSACTION_SUCCESS.getMessage();
      }
    }

    // 底层服务采用集群部署，加上重试机制，肯定可以得到返回。这里保证必须释放锁，否则会对后续的交易产生影响
    cancelTransaction(command);
    transactionService.cancelTransaction(command.getTransactionNumber());
    return TRANSACTION_FAILED.getMessage();
  }

  private boolean tryTransaction(TransactionCommand command)
      throws ExecutionException, InterruptedException {
    CompletableFuture<Results> tryTransferFrom =
        supplyAsync(() -> transferOutServiceProxy.tryTransferOut(command));
    CompletableFuture<Results> tryTransferTo =
        supplyAsync(() -> transferInServiceProxy.tryTransferIn(command));
    return tryTransferFrom.get() == TRY_SUCCESS && tryTransferTo.get() == TRY_SUCCESS;
  }

  private void confirmTransaction(TransactionCommand command) {
    CompletableFuture<Void> confirmTransferOut =
        runAsync(() -> transferOutServiceProxy.confirmTransferOut(command));
    CompletableFuture<Void> confirmTransferIn =
        runAsync(() -> transferInServiceProxy.confirmTransferIn(command));
    waitUntilFinished(confirmTransferOut, confirmTransferIn);
  }

  private void cancelTransaction(TransactionCommand command) {
    CompletableFuture<Void> cancelTransferOut =
        runAsync(() -> transferOutServiceProxy.cancelTransferOut(command));
    CompletableFuture<Void> cancelTransferIn =
        runAsync(() -> transferInServiceProxy.cancelTransferIn(command));
    waitUntilFinished(cancelTransferOut, cancelTransferIn);
  }

  private void waitUntilFinished(
      CompletableFuture<Void> transferOut, CompletableFuture<Void> transferIn) {
    while (true) {
      if (transferOut.isDone() && transferIn.isDone()) {
        break;
      }
    }
  }

  private void verifyTransactionNumber(String transactionNumber) {
    if (isBlank(transactionNumber)) {
      throw new InvalidParameterException("Transaction number invalid");
    }
  }

  private void verifyTransactionMoney(BigDecimal transactionMoney) {
    if (isNull(transactionMoney) || transactionMoney.doubleValue() < 0) {
      throw new InvalidParameterException("Transaction money invalid");
    }
  }

  private void verifyAccount(Integer transformerId, Integer transformeeId) {
    if (isNull(transformerId) || isNull(transformeeId)) {
      throw new InvalidParameterException("Transaction account invalid");
    }
    if (transformerId.equals(transformeeId)) {
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
}
