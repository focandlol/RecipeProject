package focandlol.recipeproject.airecipe.dto;

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
  private List<String> tags;
  private double temperature;
  private String extraDetails;
}
