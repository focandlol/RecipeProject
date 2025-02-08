package focandlol.recipeproject.airecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
  public static class Request{
    @NotEmpty
    private List<@NotBlank String> tags;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    private String content;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response{
    private Long id;

    private List<String> tags;

    private String name;

    private String content;
  }

}
