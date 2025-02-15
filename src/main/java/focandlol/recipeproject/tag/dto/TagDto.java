package focandlol.recipeproject.tag.dto;

import focandlol.recipeproject.tag.entity.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto {

  private Long id;

  private String name;

  public static TagDto from(TagEntity tag) {
    return TagDto.builder()
        .id(tag.getId())
        .name(tag.getName())
        .build();
  }
}
