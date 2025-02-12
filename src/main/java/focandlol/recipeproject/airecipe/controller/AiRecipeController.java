package focandlol.recipeproject.airecipe.controller;

import focandlol.recipeproject.airecipe.dto.AiRecipeDetailsDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeUpdateDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.service.AiRecipeService;
import focandlol.recipeproject.airecipe.service.ApiService;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class AiRecipeController {

  private final ApiService apiService;
  private final AiRecipeService aiRecipeService;

  @PostMapping("/airecipe")
  public String createRecipe(@AuthenticationPrincipal CustomOauth2User user,
      @RequestBody CreateAiRecipeDto createAiRecipeDto) {
    String recipe = aiRecipeService.generateRecipe(createAiRecipeDto, user);
    aiRecipeService.saveRecipe(recipe, createAiRecipeDto, user);

    return recipe;
  }

  @GetMapping("/airecipe")
  public List<AiRecipeDto> getRecipes(@AuthenticationPrincipal CustomOauth2User user,
      @RequestBody AiRecipeSearchDto aiRecipeSearchDto) {
    return aiRecipeService.getRecipes(user, aiRecipeSearchDto);
  }

  @PutMapping("/airecipe/{id}")
  public AiRecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid AiRecipeUpdateDto.Request request) {
    return aiRecipeService.updateRecipe(user, id, request);
  }

  @DeleteMapping("/airecipe/{id}")
  public void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id) {
    aiRecipeService.deleteRecipe(user, id);
  }

  @GetMapping("/airecipe/{id}")
  public AiRecipeDetailsDto getRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
  ){
    return aiRecipeService.getRecipe(user, id);
  }
}
