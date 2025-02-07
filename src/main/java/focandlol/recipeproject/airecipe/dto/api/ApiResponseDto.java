package focandlol.recipeproject.airecipe.dto.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto {
  private List<Choice> choices;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Choice{
    private Message message;
  }

}
