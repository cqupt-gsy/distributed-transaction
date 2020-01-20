package apprentice.practice.accounts.command;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateAccountCommand implements Serializable {

  private static final long serialVersionUID = -7409906242826656868L;

  private String phoneNumber;
  private String name;
  private BigDecimal balance;

}
