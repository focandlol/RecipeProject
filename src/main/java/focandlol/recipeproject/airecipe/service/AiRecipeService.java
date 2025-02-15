package focandlol.recipeproject.airecipe.service;

import focandlol.recipeproject.airecipe.dto.AiRecipeDetailsDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeUpdateDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;

public interface AiRecipeService {

  String generateRecipe(CreateAiRecipeDto createAiRecipeDto, CustomOauth2User user);

  @Transactional
  void saveRecipe(String recipe, CreateAiRecipeDto createAiRecipeDto,
      CustomOauth2User user);

  @Transactional
  AiRecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , Long id, AiRecipeUpdateDto.Request request);

  @Transactional
  void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user, Long id);

  @Transactional
  List<AiRecipeDto> getRecipes(CustomOauth2User user, AiRecipeSearchDto aiRecipeSearchDto);

  AiRecipeDetailsDto getRecipe(CustomOauth2User user, Long id);
}
