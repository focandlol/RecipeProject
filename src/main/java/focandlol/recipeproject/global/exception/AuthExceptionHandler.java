package focandlol.recipeproject.global.exception;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class AuthExceptionHandler {

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
    return ResponseEntity.status(INVALID_SIGNATURE.getStatus())
        .body(new ErrorResponse(INVALID_SIGNATURE));
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException e) {
    return ResponseEntity.status(CORRUPTED_TOKEN.getStatus())
        .body(new ErrorResponse(CORRUPTED_TOKEN));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
    return ResponseEntity.status(EXPIRED_TOKEN.getStatus()).body(new ErrorResponse(EXPIRED_TOKEN));
  }

  @ExceptionHandler(UnsupportedJwtException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException e) {
    return ResponseEntity.status(UNSUPPORTED_TOKEN.getStatus())
        .body(new ErrorResponse(UNSUPPORTED_TOKEN));
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(
      InsufficientAuthenticationException e) {
    return ResponseEntity.status(NO_TOKEN.getStatus()).body(new ErrorResponse(NO_TOKEN));
  }

}
