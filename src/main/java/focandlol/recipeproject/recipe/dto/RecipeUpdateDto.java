package focandlol.recipeproject.recipe.dto;

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

public class RecipeUpdateDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "RecipeUpdateRequest", description = "레시피 게시글 수정 요청 dto")
  public static class Request{
    @NotEmpty
    private List<@NotBlank String> tags;

    @NotBlank
    @Size(max = 40)
    private String title;

    @NotNull
    @Size(max = 40)
    private String name;

    @Size(max = 200)
    private String bonus;

    @NotNull
    private String content;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "RecipeUpdateResponse", description = "레시피 게시글 수정 응답 dto")
  public static class Response{
    private Long id;

    private List<String> tags;

    private String name;

    private String title;

    private String bonus;

    private String content;
  }

}
