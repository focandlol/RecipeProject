package focandlol.recipeproject.autocomplete.service;

import static focandlol.recipeproject.type.RedisTag.AUTOCOMPLETE;
import static focandlol.recipeproject.type.RedisTag.AUTOCOMPLETE_DELETE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoCompleteConsumer {

  private final RedisTemplate redisTemplate;

  @KafkaListener(topics = "auto-complete-topic", groupId = "autocomplete-group")
  public void addTag(List<String> tags, Acknowledgment ack) {
    System.out.println("kafka");
    for (String tag : tags) {
      String n = tag.trim();
      // 단어의 모든 접두어를 score 0으로 저장
      for (int l = 0; l <= n.length(); l++) {
        String prefix = n.substring(0, l);
        redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE.toString(), prefix, 0);

        //접두어 사용 빈도 저장
        redisTemplate.opsForHash().increment(AUTOCOMPLETE_DELETE.toString(), prefix, 1);
      }
      // 원래 단어 저장
      String perfect = n + "#";
      redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE.toString(), perfect, 0);
    }

    ack.acknowledge();
  }

  @KafkaListener(topics = "auto-complete-delete-topic", groupId = "autocomplete-group")
  public void deleteTag(List<String> tags, Acknowledgment ack) {
    System.out.println("kafka-delete");

    //원래 단어 삭제
    Object[] array = tags.stream().map(a -> a + "#").toArray();
    redisTemplate.opsForZSet().remove(AUTOCOMPLETE.toString(), array);
    for (String tag : tags) {
      String n = tag.trim();
      for (int l = 1; l <= n.length(); l++) {
        String prefix = n.substring(0, l);
        //해당 접두어 score -1
        Long score = redisTemplate.opsForHash().increment(AUTOCOMPLETE_DELETE.toString(), prefix, -1);

        // 해당 접두어 score가 0이면 다른 곳에서 사용하지 않는 접두어이므로 삭제
        if(score != null && score == 0){
          redisTemplate.opsForHash().delete(AUTOCOMPLETE_DELETE.toString(), prefix);
          redisTemplate.opsForZSet().remove(AUTOCOMPLETE.toString(), prefix);
        }
      }
    }
    ack.acknowledge();
  }
}
