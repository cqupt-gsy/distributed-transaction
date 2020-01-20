package apprentice.practice.accounts.services;

import apprentice.practice.api.enums.Status;
import apprentice.practice.api.model.AccountBackUp;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountBackUpDTO {

  private Integer id;
  private Integer userId;
  private String transactionNumber;
  private BigDecimal originalBalance;
  private BigDecimal newBalance;
  private BigDecimal transactionMoney;
  private Status status;

  public static AccountBackUpDTO createBy(AccountBackUp accountBackUp) {
    return AccountBackUpDTO.builder()
        .id(accountBackUp.getId())
        .userId(accountBackUp.getUserId())
        .transactionNumber(accountBackUp.getTransactionNumber())
        .originalBalance(accountBackUp.getOriginalBalance())
        .newBalance(accountBackUp.getNewBalance())
        .transactionMoney(accountBackUp.getTransactionMoney())
        .status(accountBackUp.getStatus())
        .build();
  }
}
