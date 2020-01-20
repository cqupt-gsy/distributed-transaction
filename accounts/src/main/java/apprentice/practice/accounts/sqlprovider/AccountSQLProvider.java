package apprentice.practice.accounts.sqlprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountSQLProvider {

  public static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id=#{userId}";

  public static final String INSERT_ACCOUNT =
      "INSERT INTO "
          + "account(phone_number, name, balance, create_at, update_at) "
          + "VALUES(#{phoneNumber}, #{name}, #{balance}, current_timestamp, current_timestamp)";
}
