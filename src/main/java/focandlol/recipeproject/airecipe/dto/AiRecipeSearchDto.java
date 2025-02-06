package focandlol.recipeproject.airecipe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeSearchDto {

  private List<String> tags;

  private String keyword;

  private boolean upper;

}
