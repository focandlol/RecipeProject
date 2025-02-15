package focandlol.recipeproject.autocomplete.service;

import static focandlol.recipeproject.type.RedisTag.AUTOCOMPLETE;
import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoCompleteService {
  private final RedisTemplate<String, String> redisTemplate;

  public Map<String,Double> getAuto(String name){
    List<String> list = getList(name);
    return sortList(list);
  }

  /**
   * addAuto, delAuto kafka 처리 -> AutoCompleteConsumer
   */

  /**
   * @param name : 오
   * '오' 가 저장된 곳 id 조회
   * 조회한 id + 500(태그 개수에 따라 바뀔 수 있음) 만큼 태그명 조회
   * name으로 시작하고 #으로 끝나는 태그명(원래 단어 eg) 오리고기)만 리턴
   */
  private List<String> getList(String name){
    Long auto = redisTemplate.opsForZSet().rank(AUTOCOMPLETE.toString(), name);

    if(auto == null){
      return Collections.emptyList();
    }

    Set<String> set = redisTemplate.opsForZSet().range(AUTOCOMPLETE.toString(), auto, auto + 500);
    if (set.isEmpty()) return Collections.emptyList();

    return set.stream()
        .takeWhile(entry -> entry.startsWith(name))
        .filter(entry -> entry.endsWith("#"))
        .map(entry -> entry.substring(0, entry.length() - 1))
        .collect(Collectors.toList());
  }

  /**
   * 자동 완성 시 사용빈도가 많은 순으로 보이도록
   * @param list : 원래 단어 들만 모인 리스트
   *
   */
  private Map<String,Double> sortList(List<String> list){
    if (list.isEmpty()) {
      return Collections.emptyMap();
    }

    // 해당 태그명에 맞는 score 조회
    List<Double> scores = redisTemplate.opsForZSet().score(TAG_RANKING.toString(), list.toArray(new String[0]));

    // 리스트에서 가져온 태그명 + score 매핑
    Map<String, Double> scoreMap = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
      scoreMap.put(list.get(i), scores.get(i));
    }

    // 내림차순 정렬 + 상위 5개만 추출 (가장 많이 사용한 태그 5개)
    return scoreMap.entrySet().stream()
        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
        .limit(5)
        .collect(Collectors.toMap(
            stringDoubleEntry -> stringDoubleEntry.getKey()
            , stringDoubleEntry1 -> stringDoubleEntry1.getValue()
            , (a, b) -> a, () -> new LinkedHashMap<>()
        ));
  }

}
