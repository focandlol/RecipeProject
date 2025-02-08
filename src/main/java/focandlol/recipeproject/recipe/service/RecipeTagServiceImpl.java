package focandlol.recipeproject.recipe.service;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.entity.RecipeTagEntity;
import focandlol.recipeproject.recipe.repository.RecipeTagRepository;
import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeTagServiceImpl implements RecipeTagService {

  private final RecipeTagRepository recipeTagRepository;

  @Override
  public void save(RecipeEntity recipe, List<TagEntity> tags) {
    recipeTagRepository.saveAll(tags.stream()
        .map(tag -> RecipeTagEntity.builder()
            .recipe(recipe)
            .tag(tag)
            .build())
        .collect(Collectors.toList()));
  }

}
