package apprentice.practice.accounts.sqlprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountSQLProvider {

  public static final String INSERT_ACCOUNT =
      "INSERT INTO "
          + "account(phone_number, name, balance, create_at, update_at) "
          + "VALUES(#{phoneNumber}, #{name}, #{balance}, #{createAt}, #{updateAt})";

  public static final String TRANSFER_FROM =
      "UPDATE account "
          + "SET balance = balance - #{balance}, update_at=current_timestamp "
          + "WHERE id=#{transferId} AND balance - #{balance} > 0";

  public static final String TRANSFER_TO =
      "UPDATE account "
          + "SET balance = balance + #{balance}, update_at=current_timestamp "
          + "WHERE id=#{transferId} AND balance + #{balance} > 0";
}
