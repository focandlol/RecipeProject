package focandlol.recipeproject.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  private final String message;

  private final HttpStatus status;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
    this.status = errorCode.getStatus();
  }
}

