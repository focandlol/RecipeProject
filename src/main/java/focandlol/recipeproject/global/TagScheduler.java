package focandlol.recipeproject.global;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagScheduler {

  private final RedisTemplate redisTemplate;
  private final JdbcTemplate jdbcTemplate;

  /**
   * redis, db 동기화 쿼리를 묶기 위해 batchUpdate, rewriteBatchedStatements=true 사용
   */
  @Scheduled(fixedRate = 20000)
  public void syncTagRanking() {
    Boolean exists = redisTemplate.hasKey("tag_update");

    if (exists) {
      // tag_update 조회 및 삭제
      Object result = getUpdateTag();

      if (result == null) {
        return;
      }

      //map으로 변환
      Map<String, Long> recipeLikeCounts = toMap(result);

      if (recipeLikeCounts == null) {
        return;
      }

      //batchUpdate
      batchUpdate(recipeLikeCounts);
    }
  }

  private void batchUpdate(Map<String, Long> recipeLikeCounts) {
    String sql = "UPDATE tag SET count = count + ? WHERE name = ?";
    List<Object[]> batchUpdateParams = recipeLikeCounts.entrySet().stream()
        .map(entry -> new Object[]{entry.getValue(),
            entry.getKey()}) // {태그 사용수, 태그명}
        .collect(Collectors.toList());

    jdbcTemplate.batchUpdate(sql, batchUpdateParams);
  }

  private static Map<String, Long> toMap(Object result) {
    if (!(result instanceof Map<?, ?> map)) {
      return null;
    }

    // map으로 변환 (key : 태그명, value : 태그 사용 횟수 변화값)
    Map<String, Long> recipeLikeCounts = map.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey().toString(),
            entry -> Long.valueOf(entry.getValue().toString())
        ));
    return recipeLikeCounts;
  }

  private Object getUpdateTag() {
    /**
     * 조회와 삭제 사이에 태그 사용이 일어나면 해당 태그 사용 횟수는 무시됨
     * 따라서 transaction으로 묶어서 원자적으로 수행
     */
    List<Object> results = (List<Object>) redisTemplate.execute(
        new SessionCallback<List<Object>>() {
          @Override
          public List<Object> execute(RedisOperations operations) throws DataAccessException {
            operations.multi();
            // 사용 횟수 변경된 태그 조회
            operations.opsForHash().entries("tag_update");
            // tag_update 삭제
            operations.delete("tag_update");
            // 트랜잭션 실행
            return operations.exec();
          }
        });

    if (results == null || results.isEmpty()) {
      return null;
    }

    Object result = results.get(0);
    return result;
  }
}
