package focandlol.recipeproject.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTagDto {

  @NotBlank
  private String tag;

  @NotBlank
  @Size(max = 20)
  private String change;
}
