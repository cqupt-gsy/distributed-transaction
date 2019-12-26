package apprentice.practice.api.services.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferCommand implements Serializable {

  private static final long serialVersionUID = 8354323237159552163L;

  private Integer transferId;
  private BigDecimal balance;

  public static TransferCommand createFrom(Integer transferId, BigDecimal transactionMoney) {
    TransferCommand transferCommand = new TransferCommand();
    transferCommand.setTransferId(transferId);
    transferCommand.setBalance(transactionMoney);
    return transferCommand;
  }

  public Integer getTransferId() {
    return transferId;
  }

  public void setTransferId(Integer transferId) {
    this.transferId = transferId;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
