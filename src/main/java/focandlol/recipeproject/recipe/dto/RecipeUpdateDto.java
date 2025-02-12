package focandlol.recipeproject.recipe.dto;

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
  public static class Request{
    @NotEmpty
    private List<@NotBlank String> tags;

    @NotBlank
    @Size(max = 20)
    private String title;

    @NotNull
    @Size(max = 20)
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
  public static class Response{
    private Long id;

    private List<String> tags;

    private String name;

    private String title;

    private String bonus;

    private String content;
  }

}
