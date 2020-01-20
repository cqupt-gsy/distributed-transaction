package apprentice.practice.transferout.repositories.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransferOutSQLProvider {

  public static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id=#{userId}";

  public static final String TRANSFER_MONEY =
      "UPDATE account "
          + "SET balance = #{newBalance}, update_at=current_timestamp "
          + "WHERE id=#{userId} AND #{newBalance} > 0";


  public static final String SELECT_ACCOUNT_BACK_UP =
      "SELECT * FROM account_back_up WHERE user_id=#{userId} AND transaction_number=#{transactionNumber}";

  public static final String INSERT_ACCOUNT_BACK_UP =
      "INSERT INTO "
          + "account_back_up(user_id, transaction_number, original_balance, new_balance, transaction_money, "
          + "status, create_at, update_at) "
          + "VALUES(#{userId}, #{transactionNumber}, #{originalBalance}, #{newBalance}, #{transactionMoney}, "
          + "#{status}, current_timestamp, current_timestamp)";

  public static final String UPDATE_ACCOUNT_BACK_UP =
      "UPDATE account_back_up "
          + "SET status=#{status}, update_at=current_timestamp "
          + "WHERE user_id=#{userId} AND transaction_number=#{transactionNumber}";


}
