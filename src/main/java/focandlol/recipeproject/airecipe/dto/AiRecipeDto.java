package focandlol.recipeproject.airecipe.dto;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  public static List<AiRecipeDto> fromEntity(List<AiRecipeEntity> list) {
    return list.stream()
        .map(entity -> AiRecipeDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .content(entity.getContent())
            .temperature(entity.getTemperature())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build()
        ).collect(Collectors.toList());
  }

}
