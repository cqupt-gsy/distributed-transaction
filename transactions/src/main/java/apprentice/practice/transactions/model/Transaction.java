package apprentice.practice.transactions.model;

import apprentice.practice.transactions.command.TransactionCommand;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

  private Integer id;
  private String transactionNumber;
  // refer to https://mybatis.org/mybatis-3/configuration.html#typeHandlers
  private BigDecimal transactionMoney;
  private Integer transformerId;
  private Integer transformeeId;
  private LocalDateTime transactionTime;
  private String envelopeId;
  private BigDecimal envelopeMoney;
  private String integralId;
  private Integer integral;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;

  public static Transaction createBy(TransactionCommand command) {
    LocalDateTime currentServerTime = LocalDateTime.now();
    return Transaction.builder()
        .transactionNumber(command.getTransactionNumber())
        .transactionMoney(command.getTransactionMoney())
        .transformerId(command.getTransformerId())
        .transformeeId(command.getTransformeeId())
        .transactionTime(currentServerTime)
        .envelopeId(command.getEnvelopeId())
        .envelopeMoney(command.getEnvelopeMoney())
        .integralId(command.getIntegralId())
        .integral(command.getIntegral())
        .createAt(currentServerTime)
        .updateAt(currentServerTime)
        .build();
  }
}
