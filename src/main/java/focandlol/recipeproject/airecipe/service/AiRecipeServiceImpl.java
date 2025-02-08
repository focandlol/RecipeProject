package focandlol.recipeproject.airecipe.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeUpdateDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import focandlol.recipeproject.airecipe.repository.AiRecipeQueryRepository;
import focandlol.recipeproject.airecipe.repository.AiRecipeRepository;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiRecipeServiceImpl implements AiRecipeService {

  private final UserRepository userRepository;
  private final TagService tagService;
  private final ApiService apiService;
  private final AiRecipeRepository aiRecipeRepository;
  private final AiRecipeTagService aiRecipeTagService;
  private final AiRecipeQueryRepository aiRecipeQueryRepository;

  @Override
  public String generateRecipe(CreateAiRecipeDto createAiRecipeDto, CustomOauth2User user) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    long count = aiRecipeRepository.countByUserId(userEntity.getId());

    //해당 사용자가 ai 레시피를 이미 10개 이상 만들었다면 예외
    if (count > 10) {
      throw new CustomException(TOO_MANY_RECIPE);
    }
    return apiService.generateRecipe(createAiRecipeDto, user);
  }

  @Override
  @Transactional
  public void saveRecipe(String recipe, CreateAiRecipeDto createAiRecipeDto,
      CustomOauth2User user) {

    List<String> seper = seperateContent(recipe);

    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    //ai_recipe 저장
    AiRecipeEntity save = aiRecipeRepository.save(AiRecipeEntity.builder()
        .name(seper.get(0))
        .content(seper.get(1))
        .extraDetails(createAiRecipeDto.getExtraDetails())
        .temperature(createAiRecipeDto.getTemperature())
        .user(userEntity).build());

    //사용 태그 저장
    tagService.add(createAiRecipeDto.getTags());

    //저장된 태그 가져오기
    List<TagEntity> tags = tagService.findByNameIn(createAiRecipeDto.getTags());

    //ai_recipe_tag 저장
    aiRecipeTagService.save(save, tags);

  }

  @Override
  @Transactional
  public AiRecipeUpdateDto.Response updateRecipe(@AuthenticationPrincipal CustomOauth2User user
      , Long id, AiRecipeUpdateDto.Request request) {

    AiRecipeEntity aiRecipe = aiRecipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(AI_RECIPE_NOT_FOUND));

    validateUpdateRecipe(user, aiRecipe);

    List<String> list = aiRecipeTagService.findTagNamesByRecipeId(id);

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

      //ai_recipe_tag 저장
      aiRecipeTagService.save(aiRecipe, getTag);
    }

    //아까 가져온 삭제할 태그들 삭제
    if (!rmTag.isEmpty()) {
      aiRecipeTagService.deleteIn(id, rmTag);
    }

    //제목, 내용 수정
    aiRecipe.updateRecipe(request.getName(), request.getContent());

    return AiRecipeUpdateDto.Response.builder()
        .id(aiRecipe.getId())
        .tags(new ArrayList<>(tags))
        .name(request.getName())
        .content(request.getContent())
        .build();

  }

  @Override
  @Transactional
  public void deleteRecipe(@AuthenticationPrincipal CustomOauth2User user, Long id) {
    validateDeleteRecipe(user, id);

    aiRecipeRepository.deleteById(id);
  }

  private void validateDeleteRecipe(CustomOauth2User user, Long id) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    AiRecipeEntity aiRecipe = aiRecipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(AI_RECIPE_NOT_FOUND));

    if (!aiRecipe.getUser().equals(userEntity)) {
      throw new CustomException(ANOTHER_USER);
    }
  }

  private void validateUpdateRecipe(CustomOauth2User user, AiRecipeEntity aiRecipe) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!aiRecipe.getUser().equals(userEntity)) {
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

  @Override
  @Transactional
  public List<AiRecipeDto> getRecipe(CustomOauth2User user, AiRecipeSearchDto aiRecipeSearchDto) {
    return AiRecipeDto.fromEntity(aiRecipeQueryRepository.findAiRecipe(user, aiRecipeSearchDto));
  }

  //레시피에서 제목, 내용 분리
  private List<String> seperateContent(String recipe) {
    List<String> list = new ArrayList<>();

    String[] parts = recipe.trim().split("\n", 2); // 첫 번째 줄(제목)과 나머지를 분리
    String title = parts[0].trim(); // 제목
    String body = parts.length > 1 ? parts[1].trim() : ""; // 본문 (없을 수도 있음)

    list.add(title);
    list.add(body);

    return list;
  }

}
