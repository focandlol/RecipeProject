package focandlol.recipeproject.tag.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;
import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import focandlol.recipeproject.autocomplete.service.AutoCompleteService;
import focandlol.recipeproject.global.exception.CustomException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TagRedisServiceImpl implements TagRedisService {

  private final RedisTemplate redisTemplate;
  private final AutoCompleteService autoCompleteService;
  private final KafkaTemplate<String, List<String>> kafkaTemplate;

  @Override
  public void saveTagInRedis(List<String> tags, double score) {
    saveTagRanking(tags, score);
    //태그 자동완성 캐시
    //autoCompleteService.addAuto(tags);

    /**
     * partition 수 3개, consumer 수 3개 설정
     */
    kafkaTemplate.send("auto-complete-topic",tags);
  }

  /**
   * @param tag 원래 태그명
   * @param change 바꿀 태그명
   */
  public void updateTagInRedis(String tag, String change){

    //이미 존재하는 태그로 수정 시 예외
    Optional.ofNullable(redisTemplate.opsForZSet().score(TAG_RANKING.toString(), change))
        .ifPresent(a -> {throw new CustomException(EXIST_TAG);});

    //원래 태그명의 score 조회
    Double score = redisTemplate.opsForZSet().score(TAG_RANKING.toString(), tag);

    //원래 태그 삭제
    deleteTagInRedis(Collections.singletonList(tag));

    //원래 태그명의 score 더해서 새로운 태그 생성
    saveTagInRedis(Collections.singletonList(change), score);
  }

  /**
   * 태그 삭제
   */
  @Override
  public void deleteTagInRedis(List<String> tags) {
    redisTemplate.opsForZSet().remove(TAG_RANKING.toString(), tags.toArray());

    //autoCompleteService.delAuto(tags);
    kafkaTemplate.send("auto-complete-delete-topic",tags);
  }

  //태그 사용 횟수 캐시
  private void saveTagRanking(List<String> tags, double score) {
    for (String tag : tags) {
      redisTemplate.opsForZSet().incrementScore(TAG_RANKING.toString(), tag, score);
    }
  }
}
