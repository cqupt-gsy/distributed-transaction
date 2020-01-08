package apprentice.practice.transactions.services;

import static apprentice.practice.api.services.enums.Results.TRANSFER_FAILED;
import static apprentice.practice.api.services.utils.SystemSleeper.sleepOneSecond;

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
      log.info("Start try transfer from for transaction number {}", command.getTransactionNumber());
      return accountService.tryTransferFrom(getTransferFromCommand(command));
    } catch (RpcException ex) {
      return TRANSFER_FAILED;
    }
  }

  // 因为Confirm操作是幂等的，所以可以不断重试
  public void confirmTransferFrom(TransactionCommand command) {
    while (true) {
      try {
        log.info("Start confirm transfer from for transaction number {}", command.getTransactionNumber());
        accountService.confirmTransferFrom(getTransferFromCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  public void cancelTransferFrom(TransactionCommand command) {
    while (true) {
      try {
        log.info("Start cancel transfer from for transaction number {}", command.getTransactionNumber());
        accountService.cancelTransferFrom(getTransferFromCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  private TransferCommand getTransferFromCommand(TransactionCommand command) {
    return getTransferCommand(command.getTransformerId(), command);
  }


  // 如果出现网络问题，直接返回失败，让客户端决定重试
  // TODO 如果是网络问题，也就是说服务直接挂，如何处理？
  public Results tryTransferTo(TransactionCommand command) {
    try {
      log.info("Start try transfer to for transaction number {}", command.getTransactionNumber());
      return accountService.tryTransferTo(getTransferToCommand(command));
    } catch (RpcException ex) {
      return TRANSFER_FAILED;
    }
  }

  // 因为Confirm操作是幂等的，所以可以不断重试
  public void confirmTransferTo(TransactionCommand command) {
    while (true) {
      try {
        log.info("Start confirm transfer to for transaction number {}", command.getTransactionNumber());
        accountService.confirmTransferTo(getTransferToCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  public void cancelTransferTo(TransactionCommand command) {
    while (true) {
      try {
        log.info("Start cancel transfer to for transaction number {}", command.getTransactionNumber());
        accountService.cancelTransferTo(getTransferToCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  private TransferCommand getTransferToCommand(TransactionCommand command) {
    return getTransferCommand(command.getTransformeeId(), command);
  }


  private TransferCommand getTransferCommand(Integer transferId, TransactionCommand command) {
    return TransferCommand.createFrom(
        transferId, command.getTransactionNumber(), command.getTransactionMoney());
  }
}
