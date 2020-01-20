package apprentice.practice.transferout.repositories;

import static apprentice.practice.transferout.repositories.sql.DistributedLockSQLProvider.INSERT_DISTRIBUTED_LOCK;
import static apprentice.practice.transferout.repositories.sql.DistributedLockSQLProvider.REMOVE_DISTRIBUTED_LOCK;
import static apprentice.practice.transferout.repositories.sql.DistributedLockSQLProvider.SELECT_DISTRIBUTED_LOCK;

import apprentice.practice.api.model.DistributedLock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DistributedLockRepository {

  @Insert(INSERT_DISTRIBUTED_LOCK)
  @Options(
      useGeneratedKeys = true,
      keyProperty = "id",
      keyColumn = "id",
      flushCache = FlushCachePolicy.TRUE)
  void saveDistributedLock(DistributedLock distributedLock);

  @Select(SELECT_DISTRIBUTED_LOCK)
  DistributedLock findDistributedLock(Integer userId);

  @Update(REMOVE_DISTRIBUTED_LOCK)
  void removeDistributedLock(Integer userId, String transactionNumber);
}
