package apprentice.practice.api.transferin.command;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferInCommand implements Serializable {

  private static final long serialVersionUID = -7250824322948301306L;

  private Integer userId;
  private String transactionNumber;
  private BigDecimal balance;

  public static TransferInCommand createFrom(
      Integer userId, String transactionNumber, BigDecimal transactionMoney) {
    return TransferInCommand.builder()
        .userId(userId)
        .transactionNumber(transactionNumber)
        .balance(transactionMoney)
        .build();
  }
}
