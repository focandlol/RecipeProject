package focandlol.recipeproject.airecipe.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.airecipe.dto.CreateAiRecipeDto;
import focandlol.recipeproject.airecipe.dto.api.ApiRequestDto;
import focandlol.recipeproject.airecipe.dto.api.ApiResponseDto;
import focandlol.recipeproject.airecipe.dto.api.Message;
import focandlol.recipeproject.airecipe.repository.AiRecipeRepository;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.global.exception.ErrorCode;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ApiService {

  private final WebClient webClient;
  private final UserRepository userRepository;
  private final AiRecipeRepository aiRecipeRepository;

  public ApiService(@Value("${openai.api.key}") String apiKey,
      @Value("${openai.api.url}") String apiUrl, AiRecipeRepository aiRecipeRepository,
      UserRepository userRepository) {
    this.webClient = WebClient.builder()
        .baseUrl(apiUrl + "/chat/completions")
        .defaultHeader("Authorization", "Bearer " + apiKey)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .build();
    this.aiRecipeRepository = aiRecipeRepository;
    this.userRepository = userRepository;
  }

  public String generateRecipe(CreateAiRecipeDto createAiRecipeDto, CustomOauth2User user) {
    //요청 내용 생성
    String content = getContent(createAiRecipeDto);

    // api 요청 메시지 생성
    Message userMessage = new Message(
        "user",
        content
    );

    // 요청 DTO 생성
    ApiRequestDto request = getRequest(userMessage, createAiRecipeDto.getTemperature());

    // WebClient 동기 요청
    return getResponse(request);
  }

  //요청 내용 생성
  private String getContent(CreateAiRecipeDto createAiRecipeDto){
    StringBuilder sb = new StringBuilder();
    sb.append("다음 내용을 포함하여 레시피를 작성해 주세요: ")
        .append(createAiRecipeDto.getTags() + ", ")
        .append("요리 제목, 필요한 재료 목록, 조리 과정, 예상 조리 시간을 반드시 포함해 주세요.\n")
        .append("중요: 한국어로 작성하고, 반드시 제목 뒤에 줄바꿈 3개를 포함하고, 재료를 요약하지 말고 세부적으로 작성해 주세요."
            + "또 조리 과정을 자세히 적어주세요.\n");

    if (createAiRecipeDto.getExtraDetails() != null && !createAiRecipeDto.getExtraDetails()
        .isEmpty()) {
      sb.append("또 다음 내용을 반영해 주세요 : " + createAiRecipeDto.getExtraDetails());
    }
    return sb.toString();
  }

  //요청 dto 생성
  private ApiRequestDto getRequest(Message message, double temperature){
    return ApiRequestDto.builder()
        .model("gpt-3.5-turbo")
        .messages(Collections.singletonList(message))
        .maxTokens(800)
        .temperature(temperature)
        .build();
  }

  private String getResponse(ApiRequestDto request){
    ApiResponseDto response = webClient.post()
        .bodyValue(request)
        .retrieve()
        .bodyToMono(ApiResponseDto.class)
        .block();

    // 응답 처리
    if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
      return response.getChoices().get(0).getMessage().getContent();
    } else {
      throw new CustomException(EMPTY_API_RESPONSE);
    }
  }
}
