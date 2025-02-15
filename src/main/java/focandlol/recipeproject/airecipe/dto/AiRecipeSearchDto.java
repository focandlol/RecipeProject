package focandlol.recipeproject.airecipe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeSearchDto {

  private String keyword;

  private boolean upper;

}
