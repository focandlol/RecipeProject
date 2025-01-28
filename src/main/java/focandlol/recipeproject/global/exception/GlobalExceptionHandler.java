package focandlol.recipeproject.global.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)
public class GlobalExceptionHandler {

  /**
   * 나중에 CustomException 같은 거 추가할 예정
   */

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponse(ErrorCode.ACCESS_DENIED));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}
