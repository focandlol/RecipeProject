package focandlol.recipeproject.airecipe.dto;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
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
public class AiRecipeDetailsDto {

  private Long id;

  private String name;

  private String content;

  private Double temperature;

  private String extraDetails;

  private List<String> tags;

  private LocalDateTime createdAt;

  public static AiRecipeDetailsDto fromEntity(AiRecipeEntity aiRecipeEntity, List<String> tags){
    return AiRecipeDetailsDto.builder()
        .id(aiRecipeEntity.getId())
        .name(aiRecipeEntity.getName())
        .content(aiRecipeEntity.getContent())
        .temperature(aiRecipeEntity.getTemperature())
        .extraDetails(aiRecipeEntity.getExtraDetails())
        .tags(tags)
        .createdAt(aiRecipeEntity.getCreatedAt())
        .build();
  }

}
