package apprentice.practice.transactions.command;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransactionCommand {
  private String transactionNumber;
  private BigDecimal transactionMoney;
  private Integer transformerId;
  private Integer transformeeId;
  private String envelopeId;
  private BigDecimal envelopeMoney;
  private String integralId;
  private Integer integral;
}
