package focandlol.recipeproject.recipe.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.dto.RecipeDetailsDto;
import focandlol.recipeproject.recipe.dto.RecipeDto;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import focandlol.recipeproject.recipe.dto.RecipeUpdateDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface RecipeService {

  RecipeCreateDto.Response addRecipe(@AuthenticationPrincipal CustomOauth2User user
      , RecipeCreateDto.Request request);

  Page<RecipeDto> getRecipes(RecipeSearchDto request, Pageable pageable);

  RecipeUpdateDto.Response updateRecipe(CustomOauth2User user, RecipeUpdateDto.Request request,
      Long id);

  void deleteRecipe(CustomOauth2User user, Long id);

  RecipeDetailsDto getRecipe(Long id);

  Page<RecipeDto> getOwnRecipes(CustomOauth2User user, Pageable pageable);

  Page<RecipeDto> getLikesRecipes(CustomOauth2User user, Pageable pageable);
}
