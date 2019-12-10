package apprentice.practice.transactions.sqlprovider;

public final class TransactionSQLProvider {

  public static final String SELECT_ALL = "SELECT * FROM transactions";

  public static final String SELECT_EXISTS_BY = "SELECT EXISTS (SELECT COUNT(id) FROM transactions WHERE transaction_number=#{transactionNumber})";

  public static final String INSERT_TRANSACTION = "INSERT INTO "
      + "transactions(transaction_number, transaction_money, transformer_account, transformer_name, transformee_account, transformee_name, status, "
      + "transaction_time, envelope_id, envelope_money, integral_id, integral, create_at, update_at) "
      + "VALUES(#{transactionNumber}, #{transactionMoney}, #{transformerAccount}, #{transformerName}, #{transformeeAccount}, #{transformeeName}, "
      + "#{status}, #{transactionTime}, #{envelopeId}, #{envelopeMoney}, #{integralId}, #{integral}, #{createAt}, #{updateAt})";

  private TransactionSQLProvider() {}
}
