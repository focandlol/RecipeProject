package focandlol.recipeproject.recipe.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.service.TagService;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final TagService tagService;
  private final RecipeTagService recipeTagService;

  @Override
  public RecipeCreateDto.Response addRecipe(@AuthenticationPrincipal CustomOauth2User user
      , RecipeCreateDto.Request request) {

    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    //레시피 저장
    RecipeEntity save = recipeRepository.save(request.toEntity(userEntity));

    //해당 레시피에서 사용된 태그 저장
    tagService.add(request.getTags());

    //저장된 태그 조회
    List<TagEntity> tags = tagService.findByNameIn(request.getTags());

    //recipe_tag 저장
    recipeTagService.save(save, tags);

    return RecipeCreateDto.Response.fromEntity(save,tags);
  }
}
