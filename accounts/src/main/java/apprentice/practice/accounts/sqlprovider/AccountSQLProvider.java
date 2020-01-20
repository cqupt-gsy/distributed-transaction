package apprentice.practice.accounts.sqlprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountSQLProvider {

  public static final String SELECT_ALL = "SELECT * FROM account";

  public static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id=#{userId}";

  public static final String SELECT_ACCOUNT_BY_IDS = "SELECT * FROM account WHERE id in ${userIds}";

  public static final String INSERT_ACCOUNT =
      "INSERT INTO "
          + "account(phone_number, name, balance, create_at, update_at) "
          + "VALUES(#{phoneNumber}, #{name}, #{balance}, current_timestamp, current_timestamp)";

  public static final String SELECT_ALL_ACCOUNT_BACK_UP = "SELECT * FROM account_back_up";

  public static final String SELECT_ACCOUNT_BACK_UP_BY_ID = "SELECT * FROM account_back_up WHERE user_id=#{userId}";

  public static final String SELECT_ACCOUNT_BACK_UP_BY_IDS = "SELECT * FROM account_back_up WHERE user_id in ${userIds}";
}
