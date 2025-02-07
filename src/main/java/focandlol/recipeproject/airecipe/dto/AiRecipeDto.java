package focandlol.recipeproject.airecipe.dto;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecipeDto {

  private Long id;

  private String name;

  private String content;

  private double temperature;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static List<AiRecipeDto> from(List<AiRecipeEntity> list) {
    List<AiRecipeDto> dtos = new ArrayList<>();
    for (AiRecipeEntity e : list) {
      dtos.add(AiRecipeDto.builder()
          .id(e.getId())
          .name(e.getName())
          .content(e.getContent())
          .temperature(e.getTemperature())
          .createdAt(e.getCreatedAt())
          .updatedAt(e.getUpdatedAt())
          .build());
    }
    return dtos;
  }

}
