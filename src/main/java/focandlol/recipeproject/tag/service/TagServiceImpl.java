package focandlol.recipeproject.tag.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.global.exception.ErrorCode;
import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.repository.TagRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final TagRedisService tagRedisService;

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

    //redis 저장
    tagRedisService.saveTagInRedis(names,1);

    return savedTags.stream()
        .map(TagDto::from)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(List<String> tags){
    //db에서 삭제
    tagRepository.deleteAllByNameIn(tags);
    //redis에서 삭제
    tagRedisService.deleteTagInRedis(tags);
  }

  /**
   * @param tag 원래 태그명
   * @param change 바꿀 태그명
   */
  public void update(String tag, String change){
    //바꾹 태그명이 이미 존재하면 예외
    tagRepository.findByName(change)
        .ifPresent(a -> {throw new CustomException(EXIST_TAG);});

    //원래 태그가 없으면 예외
    TagEntity tagEntity = tagRepository.findByName(tag)
        .orElseThrow(() -> new CustomException(INVALID_TAG));

    tagEntity.setName(change);

    //redis 수정
    tagRedisService.updateTagInRedis(tag, change);
  }
}
