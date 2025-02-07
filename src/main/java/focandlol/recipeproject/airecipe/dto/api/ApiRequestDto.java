package focandlol.recipeproject.airecipe.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestDto {
  private String model;
  private List<Message> messages;

  @JsonProperty("max_tokens")
  private int maxTokens;

  private double temperature;
}
