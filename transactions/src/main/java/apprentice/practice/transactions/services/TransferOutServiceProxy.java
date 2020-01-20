package apprentice.practice.transactions.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.utils.SystemSleeper.sleepOneSecond;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.transferout.command.TransferOutCommand;
import apprentice.practice.api.transferout.services.TransferOutService;
import apprentice.practice.transactions.command.TransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransferOutServiceProxy {

  @Reference private TransferOutService transferOutService;

  public Results tryTransferOut(TransactionCommand command) {
    try {
      log.info("Start try transfer out for transaction number {}", command.getTransactionNumber());
      return transferOutService.tryTransferOut(getTransferOutCommand(command));
    } catch (RpcException ex) {
      return TRY_FAILED;
    }
  }

  // 该阶段需要保证不断重试，直到把锁释放，在集群模式下，不会有太大问题
  public void confirmTransferOut(TransactionCommand command) {
    while (true) {
      try {
        log.info(
            "Start confirm transfer out for transaction number {}", command.getTransactionNumber());
        transferOutService.confirmTransferOut(getTransferOutCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  // 该阶段需要保证不断重试，直到把锁释放，在集群模式下，不会有太大问题
  public void cancelTransferOut(TransactionCommand command) {
    while (true) {
      try {
        log.info(
            "Start cancel transfer out for transaction number {}", command.getTransactionNumber());
        transferOutService.cancelTransferOut(getTransferOutCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  private TransferOutCommand getTransferOutCommand(TransactionCommand command) {
    return TransferOutCommand.createFrom(
        command.getTransformerId(), command.getTransactionNumber(), command.getTransactionMoney());
  }
}
