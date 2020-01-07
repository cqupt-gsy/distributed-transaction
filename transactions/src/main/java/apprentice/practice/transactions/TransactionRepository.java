package apprentice.practice.transactions;

import static apprentice.practice.transactions.sqlprovider.TransactionSQLProvider.INSERT_TRANSACTION;
import static apprentice.practice.transactions.sqlprovider.TransactionSQLProvider.SELECT_ALL;
import static apprentice.practice.transactions.sqlprovider.TransactionSQLProvider.SELECT_TRANSACTION_BY_TRANSACTION_NUMBER;

import apprentice.practice.transactions.model.Transaction;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TransactionRepository {

  @Select(SELECT_ALL)
  List<Transaction> findAll();

  @Select(SELECT_TRANSACTION_BY_TRANSACTION_NUMBER)
  Transaction selectTransaction(String transactionNumber);

  @Insert(INSERT_TRANSACTION)
  @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn="id", flushCache = FlushCachePolicy.TRUE)
  void save(Transaction transaction);
}
