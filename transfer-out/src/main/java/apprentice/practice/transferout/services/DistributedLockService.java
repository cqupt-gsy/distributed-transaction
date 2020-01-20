package apprentice.practice.transferout.services;

import static apprentice.practice.api.utils.SystemSleeper.sleepOneSecond;

import apprentice.practice.api.model.DistributedLock;
import apprentice.practice.transferout.repositories.DistributedLockRepository;
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

  private final DistributedLockRepository distributedLockRepository;

  @Autowired
  public DistributedLockService(DistributedLockRepository distributedLockRepository) {
    this.distributedLockRepository = distributedLockRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void tryLock(Integer userId, String transactionNumber) {
    while (true) {
      DistributedLock distributedLock = distributedLockRepository.findDistributedLock(userId);
      if (distributedLock != null) {
        log.info("Waiting for distributed lock for user {}", userId);
        sleepOneSecond();
      } else {
        try {
          distributedLockRepository.saveDistributedLock(
              DistributedLock.createBy(
                  userId, transactionNumber, LocalDateTime.now().plusSeconds(30)));
        } catch (DuplicateKeyException ex) {
          continue;
        }
        break;
      }
    }
  }

  @Transactional
  public void unLock(Integer userId, String transactionNumber) {
    distributedLockRepository.removeDistributedLock(userId, transactionNumber);
  }
}
