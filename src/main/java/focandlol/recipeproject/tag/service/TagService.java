package focandlol.recipeproject.tag.service;

import focandlol.recipeproject.tag.dto.TagDto;
import java.util.List;

public interface TagService {

  List<TagDto> add(List<String> names);

  void delete(List<String> tags);
  void update(String tag, String change);
}
