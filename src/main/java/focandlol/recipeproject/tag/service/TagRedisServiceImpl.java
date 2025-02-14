package focandlol.recipeproject.tag.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;
import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import focandlol.recipeproject.autocomplete.service.AutoCompleteService;
import focandlol.recipeproject.global.exception.CustomException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public void saveTagInRedis(List<String> tags, double score, double updateScore) {
    saveTagRanking(tags, score, updateScore);

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

    //태그 사용 횟수 업데이트 hash의 score 조회
    Double updateScore = Optional.ofNullable(redisTemplate.opsForHash().get("tag_update", tag))
        .map(Object::toString)
        .map(Double::parseDouble)
        .orElse(0.0);

    //원래 태그 삭제
    deleteTagInRedis(Collections.singletonList(tag));

    /**
     * 원래 태그명의 score 더해서 새로운 태그 생성
     * 소고기 -> 쇠고기 로 바꿀때
     * 소고기 사용 횟수가 100 이었다면
     * 소고기 삭제 후
     * 쇠고기에 사용 회수 100 그대로 이전
     *
     * tag_update도
     * 스케줄러 간격 간 소고기 사용 횟수 변화량 2 라면
     * 소고기 삭제 후
     * 변화량 그대로 쇠고기 변화량으로 이전
     */
    saveTagInRedis(Collections.singletonList(change), score, updateScore);
  }

  /**
   * 태그 삭제
   */
  @Override
  public void deleteTagInRedis(List<String> tags) {
    redisTemplate.opsForZSet().remove(TAG_RANKING.toString(), tags.toArray());
    redisTemplate.opsForHash().delete("tag_update", tags.toArray());
    //autoCompleteService.delAuto(tags);
    kafkaTemplate.send("auto-complete-delete-topic",tags);
  }

  /**
   * 태그 사용 횟수 캐시
   * TAG_RANKING : 해당 태그가 생성되고 난 후 지금까지의 태그 사용 횟수
   * tag_update : 태그 사용 횟수 db에 업데이트하는 스케줄러 간격(eg)20초) 간 해당 태그 사용 횟수 변화량
   * 스케줄러 간격 사이에 변화된 태그만 저장
   *
   * 스케줄러에서 db에 태그 사용횟수 업데이트
   * TAG_RANKING의 수는 점점 늘어남
   * 스케줄러에서 그 많은 사용 횟수 변화가 없는 태그들까지 다 조회해서 업데이트 하는 것은 비효율적
   *
   * 따라서 update_like로 스케줄러 사이에 사용 횟수가 변화된 태그들만 업데이트 후 update_like 삭제
   * 이 사이클 반복하여 조회 및 업데이트 최소화
   *
   * eg) tag_update 수 0개 -> 20초간 태그 사용 횟수 변경 2건
   * -> tag_update 2개
   * -> 스케줄러 실행, tag_update로 db에 update, tag_update 삭제
   * -> tag_update 0개 반복
   *
   */
  private void saveTagRanking(List<String> tags, double score, double updateScore) {
    for (String tag : tags) {
      redisTemplate.opsForZSet().incrementScore(TAG_RANKING.toString(), tag, score);
      redisTemplate.opsForHash().increment("tag_update", tag, updateScore);
    }
  }
}
