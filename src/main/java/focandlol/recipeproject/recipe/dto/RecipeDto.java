package focandlol.recipeproject.recipe.dto;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {

  private Long id;

  private String title;

  private Long count;

  private LocalDateTime created;

  public static Page<RecipeDto> fromEntity(Page<RecipeEntity> entities) {
    return entities.map(entity -> RecipeDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .count(entity.getCount())
            .created(entity.getCreatedAt())
            .build());
  }
}
