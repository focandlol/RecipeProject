package focandlol.recipeproject.recipe.service;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;

public interface RecipeTagService {

  void save(RecipeEntity recipe, List<TagEntity> tags);

  List<String> findTagNamesByRecipeId(Long id);

  void deleteIn(Long id, List<String> tags);
}
