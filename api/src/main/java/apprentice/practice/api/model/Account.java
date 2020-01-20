package apprentice.practice.api.model;

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

  public static Account createBy(String phoneNumber, String name, BigDecimal balance) {
    return Account.builder().phoneNumber(phoneNumber).name(name).balance(balance).build();
  }
}
