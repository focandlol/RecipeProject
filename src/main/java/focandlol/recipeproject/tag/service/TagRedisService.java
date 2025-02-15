package focandlol.recipeproject.tag.service;

import java.util.List;

public interface TagRedisService {

  void saveTagInRedis(List<String> tags, double score, double updateScore);

  void deleteTagInRedis(List<String> tags);

  void updateTagInRedis(String tag, String change);
}
