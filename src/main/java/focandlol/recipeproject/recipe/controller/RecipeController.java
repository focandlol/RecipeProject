package focandlol.recipeproject.recipe.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.dto.RecipeDetailsDto;
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

  /**
   * 레시피 게시글 생성
   */
  @PostMapping("/recipe")
  public RecipeCreateDto.Response addRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @RequestBody @Valid RecipeCreateDto.Request request) {
    return recipeService.addRecipe(user, request);
  }

  /**
   * 레시피 게시글 리스트 조회
   * 검색 조건 requestbody로 받기 위해 PostMapping 사용
   */
  @PostMapping("/recipe/list")
  public Page<RecipeDto> getRecipes(@RequestBody(required = false) RecipeSearchDto search,
      Pageable pageable) {
    if (search == null) {
      search = new RecipeSearchDto();
    }
    return recipeService.getRecipes(search, pageable);
  }

  /**
   * 레시피 게시글 수정
   */
  @PutMapping("/recipe/{id}")
  public RecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid RecipeUpdateDto.Request request) {

    return recipeService.updateRecipe(user, request, id);
  }

  /**
   * 레시피 게시글 삭제
   */
  @DeleteMapping("/recipe/{id}")
  public void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user, @PathVariable Long id) {
    recipeService.deleteRecipe(user, id);
  }

  /**
   * 레시피 게시글 세부 조회
   */
  @GetMapping("/recipe/{id}")
  public RecipeDetailsDto getRecipe(@PathVariable Long id) {
    return recipeService.getRecipe(id);
  }

  /**
   * 본인이 작성한 레시피 게시글 조회
   */
  @GetMapping("/recipe/own")
  public Page<RecipeDto> getOwnRecipes(@AuthenticationPrincipal CustomOauth2User user,
      Pageable pageable) {
    return recipeService.getOwnRecipes(user, pageable);
  }

  /**
   * 본인이 좋아요 누른 레시피 게시글 조회
   */
  @GetMapping("/recipe/likes")
  public Page<RecipeDto> getLikesRecipes(@AuthenticationPrincipal CustomOauth2User user,
      Pageable pageable) {
    return recipeService.getLikesRecipes(user, pageable);
  }
}
