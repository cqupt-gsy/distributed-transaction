package apprentice.practice.api.model;

import static apprentice.practice.api.enums.Status.TRY;

import apprentice.practice.api.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountBackUp {

  private Integer id;
  private Integer userId;
  private String transactionNumber;
  private BigDecimal originalBalance;
  private BigDecimal newBalance;
  private BigDecimal transactionMoney;
  private Status status;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;

  public static AccountBackUp createBy(
      Integer userId,
      String transactionNumber,
      BigDecimal originalBalance,
      BigDecimal newBalance,
      BigDecimal transactionMoney) {
    return AccountBackUp.builder()
        .userId(userId)
        .transactionNumber(transactionNumber)
        .originalBalance(originalBalance)
        .newBalance(newBalance)
        .transactionMoney(transactionMoney)
        .status(TRY)
        .build();
  }
}
