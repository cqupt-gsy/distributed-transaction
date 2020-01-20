package apprentice.practice.transactions.services;

import static apprentice.practice.api.enums.Results.TRY_FAILED;
import static apprentice.practice.api.utils.SystemSleeper.sleepOneSecond;

import apprentice.practice.api.enums.Results;
import apprentice.practice.api.transferin.command.TransferInCommand;
import apprentice.practice.api.transferin.services.TransferInService;
import apprentice.practice.transactions.command.TransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransferInServiceProxy {

  @Reference private TransferInService transferInService;

  public Results tryTransferIn(TransactionCommand command) {
    try {
      log.info("Start try transfer in for transaction number {}", command.getTransactionNumber());
      return transferInService.tryTransferIn(getTransferInCommand(command));
    } catch (RpcException ex) {
      return TRY_FAILED;
    }
  }

  // 该阶段需要保证不断重试，直到把锁释放，在集群模式下，不会有太大问题
  public void confirmTransferIn(TransactionCommand command) {
    while (true) {
      try {
        log.info(
            "Start confirm transfer in for transaction number {}", command.getTransactionNumber());
        transferInService.confirmTransferIn(getTransferInCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  // 该阶段需要保证不断重试，直到把锁释放，在集群模式下，不会有太大问题
  public void cancelTransferIn(TransactionCommand command) {
    while (true) {
      try {
        log.info(
            "Start cancel transfer in for transaction number {}", command.getTransactionNumber());
        transferInService.cancelTransferIn(getTransferInCommand(command));
        break;
      } catch (RpcException ex) {
        sleepOneSecond();
      }
    }
  }

  private TransferInCommand getTransferInCommand(TransactionCommand command) {
    return TransferInCommand.createFrom(
        command.getTransformeeId(), command.getTransactionNumber(), command.getTransactionMoney());
  }
}
