package apprentice.practice.api.services.command;

import java.io.Serializable;
import java.math.BigDecimal;

public class CreateAccountCommand implements Serializable {

  private static final long serialVersionUID = -7409906242826656868L;

  private String phoneNumber;
  private String name;
  private BigDecimal balance;

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
