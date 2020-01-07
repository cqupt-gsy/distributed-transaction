package apprentice.practice.transactions.services;

import static apprentice.practice.api.services.enums.Results.CANCEL_STATUS;
import static apprentice.practice.api.services.enums.Results.CONFIRM_STATUS;
import static apprentice.practice.api.services.enums.Results.CREATE_TRANSACTION_SUCCESS;
import static apprentice.practice.api.services.enums.Results.DUPLICATE_KEY;
import static apprentice.practice.api.services.enums.Results.TRANSFER_SUCCESS;
import static apprentice.practice.api.services.enums.Results.TRYING_STATUS;
import static apprentice.practice.api.services.enums.Results.UNKNOWN_EXCEPTION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import apprentice.practice.api.services.enums.Results;
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

  private static final String CONFIRM_MESSAGE = "";

  private static final String SUCCESS_MESSAGE = "OK! transaction success.";
  private static final String FAILED_MESSAGE = "Ops, transaction failed.";
  private final TransactionService transactionService;
  private final AccountManagerService accountManagerService;

  @Autowired
  public TransactionManagerService(
      TransactionService transactionService, AccountManagerService accountManagerService) {
    this.transactionService = transactionService;
    this.accountManagerService = accountManagerService;
  }

  // 1. 实现Try阶段需要的逻辑
  // 2. 实现Confirm阶段需要的逻辑
  // 3. 实现Cancel阶段需要的逻辑
  public String execute(TransactionCommand command) {
    verifyTransactionMoney(command.getTransactionMoney());
    verifyAccount(command.getTransformerId(), command.getTransformeeId());
    verifyEnvelope(command.getEnvelopeId(), command.getEnvelopeMoney());
    verifyIntegral(command.getIntegralId(), command.getIntegral());

    Results results = transactionService.tryStartTransaction(command);
    if (results == TRYING_STATUS
        || results == CONFIRM_STATUS
        || results == CANCEL_STATUS
        || results == DUPLICATE_KEY
        || results == UNKNOWN_EXCEPTION) {
      return results.getMessage();
    } // TODO 如果执行到这里就挂了，所有的TRY状态都无法重试，必须重新开始新交易。需要自动恢复机制？

    if (results == CREATE_TRANSACTION_SUCCESS) {
      CompletableFuture<Results> tryTransferFrom =
          supplyAsync(() -> accountManagerService.tryTransferFrom(command));
      CompletableFuture<Results> tryTransferTo =
          supplyAsync(() -> accountManagerService.tryTransferTo(command));
      try {
        if (tryTransferFrom.get() == TRANSFER_SUCCESS && tryTransferTo.get() == TRANSFER_SUCCESS) {
          return SUCCESS_MESSAGE;
        }
      } catch (InterruptedException | ExecutionException e) {
        // 三方服务发生异常后会进入该分支，该分支需要发起CANCEL流程
        return FAILED_MESSAGE;
      }
      return FAILED_MESSAGE;
    } else {
      return FAILED_MESSAGE;
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
