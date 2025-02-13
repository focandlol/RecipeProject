package focandlol.recipeproject.airecipe.service;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;

public interface AiRecipeTagService {

  void save(AiRecipeEntity save, List<TagEntity> tags);

  void deleteIn(Long id, List<String> tags);

  List<String> findTagNamesByRecipeId(Long id);
}
