// mycat auto increment key: https://blog.csdn.net/yelllowcong/article/details/79074005,
// http://deweing.github.io/2016/06/28/mycat-auto-increment/
package apprentice.practice.accounts;

import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_ACCOUNT;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BACK_UP_BY_ID;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BACK_UP_BY_IDS;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BY_ID;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BY_IDS;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ALL;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ALL_ACCOUNT_BACK_UP;

import apprentice.practice.accounts.services.AccountBackUpDTO;
import apprentice.practice.accounts.services.AccountDTO;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountRepository {

  @Insert(INSERT_ACCOUNT)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveAccount(Account account);

  @Select(SELECT_ALL)
  List<Account> findAll();

  @Select(SELECT_ACCOUNT_BY_ID)
  Account findAccountBy(Integer userId);

  @Select(SELECT_ACCOUNT_BY_IDS)
  List<Account> findAccountsBy(String userIds);

  @Select(SELECT_ALL_ACCOUNT_BACK_UP)
  List<AccountBackUp> findAllBackUp();

  @Select(SELECT_ACCOUNT_BACK_UP_BY_ID)
  List<AccountBackUp> findBackUpBy(Integer userId);

  @Select(SELECT_ACCOUNT_BACK_UP_BY_IDS)
  List<AccountBackUp> findBackUpsBy(String userIds);
}
