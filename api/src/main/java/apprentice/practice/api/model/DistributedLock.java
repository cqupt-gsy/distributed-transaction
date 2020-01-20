package apprentice.practice.api.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistributedLock {

  private Integer id;
  private Integer userId;
  private String transactionNumber;
  private LocalDateTime lockUntil;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;

  public static DistributedLock createBy(
      Integer userId, String transactionNumber, LocalDateTime lockUntil) {
    return DistributedLock.builder()
        .userId(userId)
        .transactionNumber(transactionNumber)
        .lockUntil(lockUntil)
        .build();
  }
}
