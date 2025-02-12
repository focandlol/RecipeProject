package focandlol.recipeproject.recipe.dto;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

  private String name;

  private Long count;

  private LocalDateTime created;

  public static List<RecipeDto> fromEntity(Page<RecipeEntity> entities) {
    return entities.getContent().stream()
        .map(entity -> RecipeDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .name(entity.getName())
            .count(entity.getCount())
            .created(entity.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }
}
