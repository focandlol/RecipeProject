package focandlol.recipeproject.airecipe.service;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import focandlol.recipeproject.airecipe.entity.AiRecipeTagEntity;
import focandlol.recipeproject.airecipe.repository.AiRecipeTagRepository;
import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiRecipeTagServiceImpl implements AiRecipeTagService {

  private final AiRecipeTagRepository repository;

  @Override
  public void save(AiRecipeEntity save, List<TagEntity> tags){
    repository.saveAll(tags.stream()
        .map(tag -> AiRecipeTagEntity.builder()
            .aiRecipe(save)
            .tag(tag)
            .build()).collect(Collectors.toList()));
  }

  @Override
  public void deleteIn(Long id, List<String> tags){
    repository.deleteRecipeTagIn(id, tags);
  }

  @Override
  public List<String> findTagNamesByRecipeId(Long id){
    return repository.findTagNamesByRecipeId(id);
  }

}
