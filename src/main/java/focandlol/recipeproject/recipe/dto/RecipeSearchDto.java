package focandlol.recipeproject.recipe.dto;

import focandlol.recipeproject.type.RecipeSearchType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchDto {

  private List<String> tags;

  private String keyword;

  private RecipeSearchType sortBy;

  private boolean upper;

}
