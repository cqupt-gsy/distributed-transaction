package apprentice.practice.transactions.services;

import static apprentice.practice.api.services.enums.Results.TRANSFER_FAILED;

import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.TransferCommand;
import apprentice.practice.api.services.enums.Results;
import apprentice.practice.transactions.command.TransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountManagerService {

  @Reference private AccountService accountService;

  // 如果出现异常，直接返回失败，系统恢复到失败状态，让客户端决定重试
  // TODO 如果是网络问题，也就是说服务直接挂，如何处理？
  public Results tryTransferFrom(TransactionCommand command) {
    try {
      return accountService.tryTransferFrom(
          TransferCommand.createFrom(
              command.getTransactionNumber(),
              command.getTransformerId(),
              command.getTransactionMoney()));
    } catch (RpcException ex) {
      return TRANSFER_FAILED;
    }
  }

  // 如果出现网络问题，直接返回失败，让客户端决定重试
  // TODO 如果是网络问题，也就是说服务直接挂，如何处理？
  public Results tryTransferTo(TransactionCommand command) {
    try {
      return accountService.tryTransferTo(
          TransferCommand.createFrom(
              command.getTransactionNumber(),
              command.getTransformeeId(),
              command.getTransactionMoney()));
    } catch (RpcException ex) {
      return TRANSFER_FAILED;
    }
  }
}
