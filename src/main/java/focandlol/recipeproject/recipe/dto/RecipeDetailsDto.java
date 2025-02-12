package focandlol.recipeproject.recipe.dto;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDetailsDto {
  private Long id;

  private Long userId;

  private String title;

  private String name;

  private String content;

  private String bonus;

  private Long count;

  private List<String> tags;

  private LocalDateTime createdAt;


  public static RecipeDetailsDto fromEntity(RecipeEntity recipeEntity, List<String> tags) {
    return RecipeDetailsDto.builder()
        .id(recipeEntity.getId())
        .userId(recipeEntity.getUser().getId())
        .title(recipeEntity.getTitle())
        .name(recipeEntity.getName())
        .content(recipeEntity.getContent())
        .bonus(recipeEntity.getBonus())
        .count(recipeEntity.getCount())
        .createdAt(recipeEntity.getCreatedAt())
        .tags(tags)
        .build();
  }

}
