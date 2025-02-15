package focandlol.recipeproject.airecipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAiRecipeDto {

  @NotEmpty
  private List<@NotBlank String> tags;

  @Min(0)
  @Max(1)
  private double temperature;

  @Size(max = 200)
  private String extraDetails;
}
