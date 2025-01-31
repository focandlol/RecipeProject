package focandlol.recipeproject.tag.service;

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

  private void saveRedis(TagEntity saved) {
    redisTemplate.opsForZSet()
        .add("tag_ranking", saved.getName(), saved.getCount().doubleValue());
  }
}
