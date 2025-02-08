package focandlol.recipeproject.recipe.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface RecipeService {

  RecipeCreateDto.Response addRecipe(@AuthenticationPrincipal CustomOauth2User user
      , RecipeCreateDto.Request request);
}
