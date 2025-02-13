package focandlol.recipeproject.recipe.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeController {

  private final RecipeService recipeService;

  @PostMapping("/recipe")
  public RecipeCreateDto.Response addRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @RequestBody @Valid RecipeCreateDto.Request request) {
    return recipeService.addRecipe(user, request);
  }
}
