package focandlol.recipeproject.tag.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;
import static focandlol.recipeproject.type.RedisTag.AUTOCOMPLETE;
import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.global.exception.ErrorCode;
import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.repository.TagRepository;
import focandlol.recipeproject.type.RedisTag;
import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final TagRedisService tagRedisService;
  private final RedisTemplate redisTemplate;
  private final JdbcTemplate jdbcTemplate;
  private final RedissonClient redissonClient;
  private final DataSource dataSource;

  /**
   * 태그 추가
   */
  @Override
  public List<TagDto> add(List<String> names) {
    String sql = "INSERT INTO tag (name, count, createdAt, updatedAt) " +
        "VALUES (?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE updatedAt = VALUES(updatedAt)";

    TreeSet<String> tags = new TreeSet<>(names);
    List<Object[]> batchArgs = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    List<RLock> locks = new ArrayList<>();

    long start = System.currentTimeMillis();

    try {
      for (String name : tags) {
        String lockKey = "lock:" + name;
        RLock lock = redissonClient.getLock(lockKey);
        //int count = 0;

        while (true) {
          if (lock.isLocked()) {
           // count++;
          } else {
            /**
             * count가 0이면 락을 얻기 위해 처음 시도하는 것
             * 처음 시도 시 redis에 해당 태그 있는지 확인
             * 없으면 락 얻기 시도
             *
             * count가 0이 아니면 해당 태그에 대한 락을 누군가 이미 얻은 적 있다는 것
             * 누군가 이미 얻은적 있는데 락이 안 걸려 있다 -> 이미 해당 태그가 redis, db에 저장되었다
             * 따라서 break하고 다음 태그로
             */
            //if (count == 0) {
              Double has = redisTemplate.opsForZSet().score(TAG_RANKING.toString(), name);
              if (has != null) {
                break;
              }

              boolean get = lock.tryLock(5, 10, TimeUnit.SECONDS);
              if (get) {
                locks.add(lock); // 해제할 락 목록에 추가
                batchArgs.add(new Object[]{name, 0, now, now});
                break;
              } else {
                //count++;
              }
//            } else {
//              break;
//            }
          }
        }
      }

      batchUpdate(sql, batchArgs);

      tagRedisService.saveTagInRedis(names, 1,1);

    } catch (Exception e) {
      throw new CustomException(FAILED_SAVE_TAG);
    } finally {
      for (RLock lock : locks) {
        if (lock.isHeldByCurrentThread()) {
          lock.unlock();
        }
      }
    }

    long end = System.currentTimeMillis();

    ArrayList<String> get = new ArrayList<>(tags);

    List<TagEntity> savedTags = tagRepository.findByNameIn(get);

    return savedTags.stream()
        .map(TagDto::from)
        .collect(Collectors.toList());

  }

  /**
   * 레시피, ai레시피 생성 시 태그 저장 후 바로 db에서 조회 필요 따라서 직접 commit 혹시나 누군가가 batchUpdate 하고 commit되기 전에 또 다른
   * 누군가가 select 하면 조회가 안되니까 즉시 강제 커밋
   * <p>
   * 예시) 유저 a : 사과, 배, 등등 유저 b : 사과 동시 요청 시 유저 a가 락을 얻고 batchUpdate 하고 밑에 남은 코드
   * 실행(AiRecipeService.saveRecipe) -> 아직 db에 사과 안들어감 유저 b가 유저 a 트랜잭션이 끝나기 전에
   * AiRecipeService.saveRecipe로 돌아가 select 태그 사과 태그 조회 안됨 -> 레시피 저장도 못함
   */
  private void batchUpdate(String sql, List<Object[]> batchArgs) {
    Connection connection = null;
    try {
      connection = DataSourceUtils.getConnection(dataSource);
      connection.setAutoCommit(false);

      jdbcTemplate.batchUpdate(sql, batchArgs);

      connection.commit();
    } catch (Exception e) {
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException ex) {
          throw new CustomException(FAILED_ROLLBACK);
        }
      }
      throw new CustomException(FAILED_SAVE_TAG);
    } finally {
      DataSourceUtils.releaseConnection(connection, dataSource);
    }
  }


  @Transactional
  @Override
  public void delete(List<String> tags) {
    //db에서 삭제
    tagRepository.deleteAllByNameIn(tags);
    //redis에서 삭제
    tagRedisService.deleteTagInRedis(tags);
  }

  /**
   * @param tag    원래 태그명
   * @param change 바꿀 태그명
   */
  @Transactional
  public void update(String tag, String change) {
    String lockKey = "lock:" + change;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
        //바꾹 태그명이 이미 존재하면 예외
        tagRepository.findByName(change)
            .ifPresent(a -> {
              throw new CustomException(EXIST_TAG);
            });

        //원래 태그가 없으면 예외
        TagEntity tagEntity = tagRepository.findByName(tag)
            .orElseThrow(() -> new CustomException(INVALID_TAG));

        tagEntity.setName(change);
        //redis 수정
        tagRedisService.updateTagInRedis(tag, change);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Transactional
  public List<TagEntity> findByNameIn(List<String> names) {
    return tagRepository.findByNameIn(names);
  }

  @Transactional
  public Page<TagDto> getTags(Pageable pageable) {
    return tagRepository.findAll(pageable)
        .map(entity -> TagDto.from(entity));
  }
}
