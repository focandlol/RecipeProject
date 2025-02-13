package focandlol.recipeproject.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

  private Long userId;
  private Long recipeId;

}
