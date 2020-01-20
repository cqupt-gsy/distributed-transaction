// mycat auto increment key: https://blog.csdn.net/yelllowcong/article/details/79074005,
// http://deweing.github.io/2016/06/28/mycat-auto-increment/
package apprentice.practice.accounts;

import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_ACCOUNT;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.SELECT_ACCOUNT_BY_ID;

import apprentice.practice.api.model.Account;
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

  @Select(SELECT_ACCOUNT_BY_ID)
  Account findAccountBy(Integer userId);
}
