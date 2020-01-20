package apprentice.practice.transferout.repositories.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DistributedLockSQLProvider {

  public static final String SELECT_DISTRIBUTED_LOCK =
      "SELECT * FROM distributed_lock WHERE user_id=#{userId}";

  public static final String INSERT_DISTRIBUTED_LOCK =
      "INSERT INTO "
          + "distributed_lock(user_id, transaction_number, lock_until, create_at, update_at) "
          + "VALUES(#{userId}, #{transactionNumber}, #{lockUntil}, current_timestamp, current_timestamp)";

  public static final String REMOVE_DISTRIBUTED_LOCK =
      "DELETE FROM distributed_lock "
          + "WHERE user_id=#{userId} AND transaction_number=#{transactionNumber}";
}
