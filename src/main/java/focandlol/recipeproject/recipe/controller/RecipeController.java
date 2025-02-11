package focandlol.recipeproject.recipe.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.dto.RecipeDto;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import focandlol.recipeproject.recipe.dto.RecipeUpdateDto;
import focandlol.recipeproject.recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/recipe")
  public Page<RecipeDto> getRecipes(@RequestBody(required = false) RecipeSearchDto search,
      Pageable pageable) {
    if (search == null) {
      search = new RecipeSearchDto();
    }
    return recipeService.getRecipes(search, pageable);
  }

  @PutMapping("/recipe/{id}")
  public RecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid RecipeUpdateDto.Request request) {

    return recipeService.updateRecipe(user, request, id);
  }

  @DeleteMapping("/recipe/{id}")
  public void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user, @PathVariable Long id) {
    recipeService.deleteRecipe(user, id);
  }
}
