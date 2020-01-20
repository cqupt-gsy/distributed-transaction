package apprentice.practice.transferin.repositories;

import static apprentice.practice.transferin.repositories.sql.TransferInSQLProvider.INSERT_ACCOUNT_BACK_UP;
import static apprentice.practice.transferin.repositories.sql.TransferInSQLProvider.SELECT_ACCOUNT_BACK_UP;
import static apprentice.practice.transferin.repositories.sql.TransferInSQLProvider.SELECT_ACCOUNT_BY_ID;
import static apprentice.practice.transferin.repositories.sql.TransferInSQLProvider.TRANSFER_MONEY;
import static apprentice.practice.transferin.repositories.sql.TransferInSQLProvider.UPDATE_ACCOUNT_BACK_UP;

import apprentice.practice.api.enums.Status;
import apprentice.practice.api.model.Account;
import apprentice.practice.api.model.AccountBackUp;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TransferInRepository {

  @Select(SELECT_ACCOUNT_BY_ID)
  Account findAccount(Integer userId);

  @Update(TRANSFER_MONEY)
  boolean transfer(Integer userId, BigDecimal newBalance);

  @Insert(INSERT_ACCOUNT_BACK_UP)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveRollback(AccountBackUp accountBackUp);

  @Select(SELECT_ACCOUNT_BACK_UP)
  AccountBackUp findRollback(Integer userId, String transactionNumber);

  @Update(UPDATE_ACCOUNT_BACK_UP)
  void updateRollback(Integer userId, String transactionNumber, Status status);
}
