package focandlol.recipeproject.airecipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AiRecipeUpdateDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "AiRecipeUpdateRequest", description = "AI 레시피 수정 요청 DTO")
  public static class Request{
    @NotEmpty
    private List<@NotBlank String> tags;

    @NotNull
    @Size(max = 40)
    private String name;

    @NotNull
    private String content;

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "AiRecipeUpdateResponse", description = "AI 레시피 수정 응답 DTO")
  public static class Response{
    private Long id;

    private List<String> tags;

    private String name;

    private String content;
  }

}
