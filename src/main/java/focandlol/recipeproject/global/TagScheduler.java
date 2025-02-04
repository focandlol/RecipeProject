package focandlol.recipeproject.global;

import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagScheduler {

  private final RedisTemplate redisTemplate;
  private final JdbcTemplate jdbcTemplate;
  private static final String QUERY =
      "INSERT INTO tag (name, count, createdat, updatedat) " +
          "VALUES (?, ?, ?, ?) " +
          "ON DUPLICATE KEY UPDATE count = VALUES(count), updatedat = updatedat;";

  /**
   * redis, db 동기화 insert 쿼리를 묶기 위해 batchUpdate, rewriteBatchedStatements=true 사용
   */
  @Scheduled(fixedRate = 10000)
  @Transactional
  public void syncTagRanking() {
    Set<ZSetOperations.TypedTuple<String>> tagRanking = getTagRanking();

    if (tagRanking == null || tagRanking.isEmpty()) {
      return;
    }

    List<Object[]> batchArgs = prepareBatchArgs(tagRanking);

    executeBatchUpdate(batchArgs);
  }

  private void executeBatchUpdate(List<Object[]> batchArgs) {
    jdbcTemplate.batchUpdate(QUERY, batchArgs);
  }

  private List<Object[]> prepareBatchArgs(Set<ZSetOperations.TypedTuple<String>> tagRanking) {
    List<Object[]> batchArgs = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (ZSetOperations.TypedTuple<String> tuple : tagRanking) {
      batchArgs.add(new Object[]{tuple.getValue(), tuple.getScore(), now, now});
    }

    return batchArgs;
  }

  private Set<ZSetOperations.TypedTuple<String>> getTagRanking() {
    return redisTemplate.opsForZSet().rangeWithScores(TAG_RANKING.toString(), 0, -1);
  }

}
