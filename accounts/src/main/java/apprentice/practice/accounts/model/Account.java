package apprentice.practice.accounts.model;

import apprentice.practice.api.services.command.CreateAccountCommand;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

  private Integer id;
  private String phoneNumber;
  private String name;
  private BigDecimal balance;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;

  public static Account createBy(CreateAccountCommand createAccountCommand) {
    LocalDateTime currentServerTime = LocalDateTime.now();
    return Account.builder()
        .phoneNumber(createAccountCommand.getPhoneNumber())
        .name(createAccountCommand.getName())
        .balance(createAccountCommand.getBalance())
        .createAt(currentServerTime)
        .updateAt(currentServerTime)
        .build();
  }

}
