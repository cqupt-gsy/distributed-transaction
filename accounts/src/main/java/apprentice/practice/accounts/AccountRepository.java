// mycat auto increment key: https://blog.csdn.net/yelllowcong/article/details/79074005,
// http://deweing.github.io/2016/06/28/mycat-auto-increment/
package apprentice.practice.accounts;

import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.INSERT_ACCOUNT;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.TRANSFER_FROM;
import static apprentice.practice.accounts.sqlprovider.AccountSQLProvider.TRANSFER_TO;

import apprentice.practice.accounts.model.Account;
import apprentice.practice.api.services.command.TransferCommand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountRepository {

  @Insert(INSERT_ACCOUNT)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void save(Account account);

  @Update(TRANSFER_FROM)
  boolean transferFrom(TransferCommand command);

  @Update(TRANSFER_TO)
  boolean transferTo(TransferCommand command);
}
