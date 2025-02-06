package focandlol.recipeproject.airecipe.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.airecipe.dto.AiRecipeDto;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import focandlol.recipeproject.airecipe.entity.AiRecipeTagEntity;
import focandlol.recipeproject.airecipe.repository.AiRecipeQueryRepository;
import focandlol.recipeproject.airecipe.repository.AiRecipeRepository;
import focandlol.recipeproject.airecipe.repository.AiRecipeTagRepository;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.global.exception.ErrorCode;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.tag.service.TagService;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AiRecipeService {

  private final UserRepository userRepository;
  private final TagService tagService;
  private final AiRecipeRepository aiRecipeRepository;
  private final AiRecipeTagRepository aiRecipeTagRepository;
  private final AiRecipeQueryRepository aiRecipeQueryRepository;

  public void saveRecipe(String recipe, CreateAiRecipeDto createAiRecipeDto, CustomOauth2User user){

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

    List<AiRecipeTagEntity> list = new ArrayList<>();

    for (TagEntity tag : tags) {
      list.add(AiRecipeTagEntity.builder().aiRecipe(save).tag(tag).build());
    }

    //ai_recipe_tag 저장
    aiRecipeTagRepository.saveAll(list);

  }

  public List<AiRecipeDto> getRecipe(CustomOauth2User user, AiRecipeSearchDto aiRecipeSearchDto){
    return AiRecipeDto.from(aiRecipeQueryRepository.findAiRecipe(user,aiRecipeSearchDto));
  }

  //레시피에서 제목, 내용 분리
  private List<String> seperateContent(String recipe){
    List<String> list = new ArrayList<>();

    String[] parts = recipe.trim().split("\n", 2); // 첫 번째 줄(제목)과 나머지를 분리
    String title = parts[0].trim(); // 제목
    String body = parts.length > 1 ? parts[1].trim() : ""; // 본문 (없을 수도 있음)

    list.add(title);
    list.add(body);

    return list;
  }

}
