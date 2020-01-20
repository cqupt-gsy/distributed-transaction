package apprentice.practice.accounts.services;

import apprentice.practice.api.model.Account;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {

  private Integer id;
  private String phoneNumber;
  private String name;
  private BigDecimal balance;

  public static AccountDTO createBy(Account account) {
    return AccountDTO.builder()
        .id(account.getId())
        .phoneNumber(account.getPhoneNumber())
        .name(account.getName())
        .balance(account.getBalance())
        .build();
  }
}
