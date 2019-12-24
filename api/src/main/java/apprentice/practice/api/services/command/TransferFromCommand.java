package apprentice.practice.api.services.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferFromCommand implements Serializable {

  private static final long serialVersionUID = 8354323237159552163L;

  private String phoneNumber;
  private String name;
  private BigDecimal balance;

  public static TransferFromCommand createFrom(
      String transformerAccount, String transformerName, BigDecimal transactionMoney) {
    TransferFromCommand transferFromCommand = new TransferFromCommand();
    transferFromCommand.setPhoneNumber(transformerAccount);
    transferFromCommand.setName(transformerName);
    transferFromCommand.setBalance(transactionMoney);
    return transferFromCommand;
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
