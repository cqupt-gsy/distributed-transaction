package apprentice.practice.api.services.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferCommand implements Serializable {

  private static final long serialVersionUID = 8354323237159552163L;

  private String transactionNumber;
  private Integer transferId;
  private BigDecimal balance;

  public static TransferCommand createFrom(
      String transactionNumber, Integer transferId, BigDecimal transactionMoney) {
    TransferCommand transferCommand = new TransferCommand();
    transferCommand.setTransactionNumber(transactionNumber);
    transferCommand.setTransferId(transferId);
    transferCommand.setBalance(transactionMoney);
    return transferCommand;
  }

  public String getTransactionNumber() {
    return transactionNumber;
  }

  public void setTransactionNumber(String transactionNumber) {
    this.transactionNumber = transactionNumber;
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
