package apprentice.practice.api.transferout.command;

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
public class TransferOutCommand implements Serializable {

  private static final long serialVersionUID = 8354323237159552163L;

  private Integer userId;
  private String transactionNumber;
  private BigDecimal balance;

  public static TransferOutCommand createFrom(
      Integer userId, String transactionNumber, BigDecimal transactionMoney) {
    return TransferOutCommand.builder()
        .userId(userId)
        .transactionNumber(transactionNumber)
        .balance(transactionMoney)
        .build();
  }
}
