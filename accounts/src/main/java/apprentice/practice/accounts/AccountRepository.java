// mycat auto increment key: https://blog.csdn.net/yelllowcong/article/details/79074005,
// http://deweing.github.io/2016/06/28/mycat-auto-increment/
package apprentice.practice.accounts;

import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_ACCOUNT;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_ACCOUNT_BACK_UP;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_DISTRIBUTED_LOCK;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BACK_UP;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BY_ID;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_DISTRIBUTED_LOCK;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.TRANSFER_MONEY;

import apprentice.practice.accounts.model.Account;
import apprentice.practice.accounts.model.AccountBackUp;
import apprentice.practice.accounts.model.DistributedLock;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountRepository {

  @Insert(INSERT_ACCOUNT)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveAccount(Account account);

  @Insert(INSERT_ACCOUNT_BACK_UP)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveAccountBackUp(AccountBackUp accountBackUp);

  @Insert(INSERT_DISTRIBUTED_LOCK)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveDistributedLock(DistributedLock distributedLock);

  @Select(SELECT_ACCOUNT_BY_ID)
  Account findAccountBy(Integer transferId);

  @Update(TRANSFER_MONEY)
  boolean transferMoney(Integer userId, BigDecimal newBalance);

  @Select(SELECT_ACCOUNT_BACK_UP)
  AccountBackUp findAccountBackUp(Integer userId, String transactionNumber);

  @Select(SELECT_DISTRIBUTED_LOCK)
  DistributedLock findDistributedLock(Integer userId);

}
