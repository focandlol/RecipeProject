package focandlol.recipeproject.tag.service;

import static focandlol.recipeproject.type.RedisTag.TAG_RANKING;

import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.repository.TagRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final RedisTemplate redisTemplate;

  /**
   * 태그 추가
   */
  @Override
  public List<TagDto> add(List<String> names) {
    Set<String> exist = tagRepository.findByNameIn(names)
        .stream().map(tags -> tags.getName()).collect(Collectors.toSet());

    List<TagEntity> newTags = names.stream()
        .filter(name -> !exist.contains(name))
        .map(name -> TagEntity.builder().name(name).build())
        .collect(Collectors.toList());

    List<TagEntity> savedTags = (List<TagEntity>) tagRepository.saveAll(newTags);

    return savedTags.stream()
        .peek(saved -> saveRedis(saved))
        .map(TagDto::from)
        .collect(Collectors.toList());
  }

  /**
   * redis에 태그 저장
   */
  private void saveRedis(TagEntity saved) {
    redisTemplate.opsForZSet()
        .add(TAG_RANKING.toString(), saved.getName(), saved.getCount().doubleValue());
  }
}
