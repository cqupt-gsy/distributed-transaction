package apprentice.practice.transactions.services;

import apprentice.practice.api.services.accounts.AccountService;
import apprentice.practice.api.services.command.TransferCommand;
import apprentice.practice.transactions.command.TransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountManagerService {

  @Reference private AccountService accountService;

  // 增加重试机制处理网络问题
  public boolean tryTransferFrom(TransactionCommand command) {
    int retryTimes = 0;
    while (true) {
      try {
        return accountService.tryTransferFrom(
            TransferCommand.createFrom(command.getTransformerId(), command.getTransactionMoney()));
      } catch (RpcException ex) {
        if (ex.isNetwork() || ex.isTimeout()) {
          if (++retryTimes > 3) {
            break;
          }
          log.error(
              "TryTransferFrom failed with network error {}, start retry times {}",
              ex.getMessage(),
              retryTimes);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return false;
  }

  // 增加重试机制处理网络问题
  public boolean tryTransferTo(TransactionCommand command) {
    int retryTimes = 0;
    while (true) {
      try {
        return accountService.tryTransferTo(
            TransferCommand.createFrom(command.getTransformeeId(), command.getTransactionMoney()));
      } catch (RpcException ex) {
        if (ex.isNetwork() || ex.isTimeout()) {
          if (++retryTimes > 3) {
            break;
          }
          log.error(
              "TryTransferTo failed with network error {}, start retry times {}",
              ex.getMessage(),
              retryTimes);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return false;
  }
}
