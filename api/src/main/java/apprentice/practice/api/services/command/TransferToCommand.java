package apprentice.practice.api.services.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferToCommand implements Serializable {

  private static final long serialVersionUID = -6302223122548976079L;

  private String phoneNumber;
  private String name;
  private BigDecimal balance;

  public static TransferToCommand createFrom(
      String transformeeAccount, String transformeeName, BigDecimal transactionMoney) {
    TransferToCommand transferToCommand = new TransferToCommand();
    transferToCommand.setPhoneNumber(transformeeAccount);
    transferToCommand.setName(transformeeName);
    transferToCommand.setBalance(transactionMoney);
    return transferToCommand;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
