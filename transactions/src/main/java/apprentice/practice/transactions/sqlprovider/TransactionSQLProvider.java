package apprentice.practice.transactions.sqlprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSQLProvider {

  public static final String SELECT_ALL = "SELECT * FROM transactions";

  public static final String SELECT_TRANSACTION_BY_TRANSACTION_NUMBER =
      "SELECT * FROM transactions WHERE transaction_number=#{transactionNumber} ";

  public static final String INSERT_TRANSACTION =
      "INSERT INTO "
          + "transactions(transaction_number, transaction_money, transformer_id, transformee_id, transaction_time, "
          + "envelope_id, envelope_money, integral_id, integral, status, create_at, update_at) "
          + "VALUES(#{transactionNumber}, #{transactionMoney}, #{transformerId}, #{transformeeId}, current_timestamp, "
          + "#{envelopeId}, #{envelopeMoney}, #{integralId}, #{integral}, #{status}, current_timestamp, current_timestamp)";

  public static final String UPDATE_TRANSACTION =
      "UPDATE transactions "
          + "SET status=#{status}, update_at=current_timestamp "
          + "WHERE transaction_number=#{transactionNumber}";
}
