package focandlol.recipeproject.airecipe.controller;

import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.service.AiRecipeService;
import focandlol.recipeproject.airecipe.service.ApiService;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    return aiRecipeService.generateRecipe(createAiRecipeDto, user);
  }

  @GetMapping("/airecipe")
  public List<AiRecipeDto> getRecipe(@AuthenticationPrincipal CustomOauth2User user,
      @RequestBody AiRecipeSearchDto aiRecipeSearchDto) {
    return aiRecipeService.getRecipe(user, aiRecipeSearchDto);
  }
}
