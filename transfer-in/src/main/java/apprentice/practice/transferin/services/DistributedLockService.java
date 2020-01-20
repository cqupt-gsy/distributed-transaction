package apprentice.practice.transferin.services;

import static apprentice.practice.api.utils.SystemSleeper.sleepOneSecond;

import apprentice.practice.api.model.DistributedLock;
import apprentice.practice.transferin.repositories.DistributedLockRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DistributedLockService {

  private static final int LOCK_TIME = 30; // 锁定时间，暂时没有使用自释放锁
  private final DistributedLockRepository distributedLockRepository;

  @Autowired
  public DistributedLockService(DistributedLockRepository distributedLockRepository) {
    this.distributedLockRepository = distributedLockRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void tryLock(Integer userId, String transactionNumber) {
    int reTryTimes = 10; // Time out is 10 seconds, so after retry 10 times, client already timeout
    while (reTryTimes > 0) {
      DistributedLock distributedLock = distributedLockRepository.findDistributedLock(userId);
      if (distributedLock != null) {
        // 允许重入锁，同一笔交易可以重试
        if (distributedLock.getTransactionNumber().equals(transactionNumber)) {
          break;
        }
        log.info("Waiting for distributed lock for user {}", userId);
        sleepOneSecond();
        --reTryTimes;
      } else {
        try {
          distributedLockRepository.saveDistributedLock(
              DistributedLock.createBy(
                  userId, transactionNumber, LocalDateTime.now().plusSeconds(LOCK_TIME)));
        } catch (DuplicateKeyException ex) {
          continue;
        }
        break;
      }
    }
    if (reTryTimes <= 0) {
      log.error("Waiting for distributed lock long time for user {}", userId);
      throw new RuntimeException("Waiting for distributed lock long time for user " + userId);
    }
  }

  @Transactional
  public void unLock(Integer userId, String transactionNumber) {
    distributedLockRepository.removeDistributedLock(userId, transactionNumber);
  }
}
