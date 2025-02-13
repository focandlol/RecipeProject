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
public class LikeScheduler {

  private final RedisTemplate redisTemplate;
  private final JdbcTemplate jdbcTemplate;

  @Scheduled(fixedRate = 20000)
  public void syncLike() {
    //upate_like : 변경된 좋아요가 있는 지 확인
    Boolean exists = redisTemplate.hasKey("update_like");

    if (exists) {
      /**
       * 조회와 삭제 사이에 좋아요 변경이 일어나면 해당 좋아요는 무시됨
       * 따라서 transaction으로 묶어서 원자적으로 수행
       */
      List<Object> results = (List<Object>) redisTemplate.execute(new SessionCallback<List<Object>>() {
        @Override
        public List<Object> execute(RedisOperations operations) throws DataAccessException {
          operations.multi();
          // 좋아요 변경된 레시피 조회
          operations.opsForHash().entries("update_like");
          // update_like 삭제
          operations.delete("update_like");
          // 트랜잭션 실행
          return operations.exec();
        }
      });

      if (results == null || results.isEmpty()) {
        return;
      }

      // operations.opsForHash().entries("update_like"); 결과
      Object result = results.get(0);

      if (!(result instanceof Map<?, ?> map)) {
        return;
      }

      // map으로 변환 (key : 레시피 id, value : 좋아요 수 변화 값)
      Map<String, Long> recipeLikeCounts = map.entrySet().stream()
          .collect(Collectors.toMap(
              entry -> entry.getKey().toString(),
              entry -> Long.valueOf(entry.getValue().toString())
          ));


      //batchUpdate
      String sql = "UPDATE recipe SET count = count + ? WHERE recipe_id = ?";
      List<Object[]> batchUpdateParams = recipeLikeCounts.entrySet().stream()
          .map(entry -> new Object[]{entry.getValue(), Long.parseLong(entry.getKey())}) // {좋아요, 레시피 id}
          .collect(Collectors.toList());

      jdbcTemplate.batchUpdate(sql, batchUpdateParams);
    }
  }

}
