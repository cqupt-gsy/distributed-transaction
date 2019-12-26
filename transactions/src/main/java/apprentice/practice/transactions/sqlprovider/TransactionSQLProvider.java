package apprentice.practice.transactions.sqlprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSQLProvider {

  public static final String SELECT_ALL = "SELECT * FROM transactions";

  public static final String SELECT_EXISTS_BY =
      "SELECT EXISTS (SELECT 1 FROM transactions WHERE transaction_number=#{transactionNumber})";

  public static final String INSERT_TRANSACTION =
      "INSERT INTO "
          + "transactions(transaction_number, transaction_money, transformer_id, transformee_id, "
          + "transaction_time, envelope_id, envelope_money, integral_id, integral, create_at, update_at) "
          + "VALUES(#{transactionNumber}, #{transactionMoney}, #{transformerId}, #{transformeeId}, "
          + "#{transactionTime}, #{envelopeId}, #{envelopeMoney}, #{integralId}, #{integral}, #{createAt}, #{updateAt})";
}
