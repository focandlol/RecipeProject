package focandlol.recipeproject.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

  private ErrorCode errorCode;

  private String message;

  private HttpStatus status;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
    this.status = errorCode.getStatus();
  }
}
