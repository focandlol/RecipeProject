package focandlol.recipeproject.recipe.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.recipe.dto.RecipeCreateDto;
import focandlol.recipeproject.recipe.dto.RecipeDetailsDto;
import focandlol.recipeproject.recipe.dto.RecipeDto;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import focandlol.recipeproject.recipe.dto.RecipeUpdateDto;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.repository.QueryRecipeRepository;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.service.TagService;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImpl implements RecipeService {

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final QueryRecipeRepository queryRecipeRepository;
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

    return RecipeCreateDto.Response.fromEntity(save, tags);
  }

  @Override
  public Page<RecipeDto> getRecipes(RecipeSearchDto request, Pageable pageable) {
    Page<RecipeEntity> recipes = queryRecipeRepository.findRecipes(request, pageable);

    return recipes.map(entity ->
        RecipeDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .name(entity.getName())
            .count(entity.getCount())
            .created(entity.getCreatedAt())
            .build()
    );
  }

  @Override
  public RecipeUpdateDto.Response updateRecipe(CustomOauth2User user,
      RecipeUpdateDto.Request request, Long id) {
    RecipeEntity recipeEntity = recipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    validateUpdateRecipe(user, recipeEntity);

    List<String> list = recipeTagService.findTagNamesByRecipeId(id);

    Set<String> tags = new HashSet<>(request.getTags());

    //삭제할 태그들
    //원래 태그들 중에서 변경될 태그들에 포함되지 태그들 삭제
    List<String> rmTag = findRemove(list, tags);

    //추가될 태그들
    //원래 저장되어 있던 태그들에 포함되지 않은 태그들 저장
    List<String> save = findSave(list, tags);

    //태그 저장
    if (!save.isEmpty()) {
      tagService.add(save);

      //저장한 태그 조회
      List<TagEntity> getTag = tagService.findByNameIn(save);

      //recipe_tag 저장
      recipeTagService.save(recipeEntity, getTag);
    }

    //아까 가져온 삭제할 태그들 삭제
    if (!rmTag.isEmpty()) {
      recipeTagService.deleteIn(id, rmTag);
    }

    //제목, 레시피명, 내용, 추가 설명 수정
    recipeEntity.updateRecipe(request.getTitle(), request.getName(), request.getContent(),
        request.getBonus());

    return RecipeUpdateDto.Response.builder()
        .id(recipeEntity.getId())
        .tags(new ArrayList<>(tags))
        .title(request.getTitle())
        .name(request.getName())
        .content(request.getContent())
        .bonus(request.getBonus())
        .build();
  }

  /**
   * 레시피 삭제
   */
  @Override
  public void deleteRecipe(CustomOauth2User user, Long id){
    validateDeleteRecipe(user, id);

    recipeRepository.deleteById(id);
  }

  /**
   * 레시피 세부 사항 조회
   */
  @Override
  public RecipeDetailsDto getRecipe(Long id){
    RecipeEntity recipeEntity = recipeRepository.findByIdFetch(id)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    List<String> tags = recipeTagService.findTagNamesByRecipeId(id);

    return RecipeDetailsDto.fromEntity(recipeEntity, tags);
  }

  /**
   * 본인이 작성한 레시피 조회
   */
  @Override
  public Page<RecipeDto> getOwnRecipes(CustomOauth2User user, Pageable pageable) {
    Page<RecipeEntity> pages = recipeRepository.findByUserId(user.getId(), pageable);

    return pages.map(entity ->
        RecipeDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .name(entity.getName())
            .count(entity.getCount())
            .created(entity.getCreatedAt())
            .build()
    );
  }

  /**
   * 본인이 좋아요 누른 레시피 조회
   */
  @Override
  public Page<RecipeDto> getLikesRecipes(CustomOauth2User user, Pageable pageable) {
    Page<RecipeEntity> pages = recipeRepository.findLikeRecipeByUserId(user.getId(),
        pageable);

    return pages.map(entity ->
        RecipeDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .name(entity.getName())
            .count(entity.getCount())
            .created(entity.getCreatedAt())
            .build()
    );
  }

  private void validateDeleteRecipe(CustomOauth2User user, Long id) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    RecipeEntity recipeEntity = recipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    if (!recipeEntity.getUser().equals(userEntity)) {
      throw new CustomException(ANOTHER_USER);
    }
  }

  private void validateUpdateRecipe(CustomOauth2User user, RecipeEntity recipe) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!recipe.getUser().equals(userEntity)) {
      throw new CustomException(ANOTHER_USER);
    }
  }

  private List<String> findSave(List<String> list, Set<String> tags) {
    return tags.stream()
        .filter(tag -> !list.contains(tag))
        .collect(Collectors.toList());
  }

  private List<String> findRemove(List<String> list, Set<String> tags) {
    return list.stream()
        .filter(tag -> !tags.contains(tag))
        .collect(Collectors.toList());
  }
}
