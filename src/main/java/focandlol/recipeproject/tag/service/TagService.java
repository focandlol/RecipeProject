package focandlol.recipeproject.tag.service;

import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

  List<TagDto> add(List<String> names);
  List<TagEntity> findByNameIn(List<String> names);

  void delete(List<String> tags);
  void update(String tag, String change);

  Page<TagDto> getTags(Pageable pageable);
}
