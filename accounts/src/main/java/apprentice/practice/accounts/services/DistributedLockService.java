package apprentice.practice.accounts.services;

import static apprentice.practice.api.services.utils.SystemSleeper.sleepOneSecond;

import apprentice.practice.accounts.AccountRepository;
import apprentice.practice.accounts.model.DistributedLock;
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

  private final AccountRepository accountRepository;

  @Autowired
  public DistributedLockService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  // 看日志是否这里有一次提交
  public void tryLock(Integer userId, String transactionNumber) {
    while (true) { // 加锁，一个用户同时只能参加一笔交易
      DistributedLock distributedLock = accountRepository.findDistributedLock(userId);
      if (distributedLock != null) { // 需要处理锁超时的逻辑
        log.info("Waiting for distributed lock for user {}", userId);
        sleepOneSecond();
      } else {
        try {
          accountRepository.saveDistributedLock(
              DistributedLock.createBy(
                  userId, transactionNumber, LocalDateTime.now().plusSeconds(30)));
        } catch (DuplicateKeyException ex) {
          continue;
        }
        break;
      }
    }
  }
}
