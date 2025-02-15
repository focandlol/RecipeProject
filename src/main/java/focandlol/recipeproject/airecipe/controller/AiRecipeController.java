package focandlol.recipeproject.airecipe.controller;

import focandlol.recipeproject.airecipe.dto.AiRecipeDetailsDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeUpdateDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.service.AiRecipeService;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiRecipeController {

  private final AiRecipeService aiRecipeService;

  /**
   * ai레시피 생성
   */
  @PostMapping("/airecipe")
  public String createRecipe(@AuthenticationPrincipal CustomOauth2User user,
      @RequestBody @Valid CreateAiRecipeDto createAiRecipeDto) {
    String recipe = aiRecipeService.generateRecipe(createAiRecipeDto, user);
    aiRecipeService.saveRecipe(recipe, createAiRecipeDto, user);

    return recipe;
  }

  /**
   * ai레시피 리스트 조회
   * 검색 조건 requestbody로 받기 위해 PostMapping 사용
   */
  @GetMapping("/airecipe")
  public List<AiRecipeDto> getRecipes(@AuthenticationPrincipal CustomOauth2User user,
      @ModelAttribute AiRecipeSearchDto aiRecipeSearchDto, @RequestParam List<String> tags) {
    return aiRecipeService.getRecipes(user, aiRecipeSearchDto, tags);
  }

  /**
   * ai레시피 수정
   */
  @PutMapping("/airecipe/{id}")
  @Operation(summary = "AI 레시피 수정", description = "AI 레시피 정보를 수정합니다.")
  public AiRecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid AiRecipeUpdateDto.Request request) {
    return aiRecipeService.updateRecipe(user, id, request);
  }

  /**
   * ai레시피 삭제
   */
  @DeleteMapping("/airecipe/{id}")
  public void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id) {
    aiRecipeService.deleteRecipe(user, id);
  }

  /**
   * ai레시피 세부 조회
   */
  @GetMapping("/airecipe/{id}")
  public AiRecipeDetailsDto getRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
  ){
    return aiRecipeService.getRecipe(user, id);
  }
}
