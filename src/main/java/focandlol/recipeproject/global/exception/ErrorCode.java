package focandlol.recipeproject.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  /**
   * 인증 에러
   */
  CORRUPTED_TOKEN("토큰이 손상되었습니다.", BAD_REQUEST),
  EXPIRED_TOKEN("만료된 토큰입니다.", UNAUTHORIZED),
  INVALID_SIGNATURE("잘못된 시그니처입니다.", UNAUTHORIZED),
  UNSUPPORTED_TOKEN("지원하지 않는 토큰 형식입니다.", BAD_REQUEST),
  NO_TOKEN("토큰이 없습니다",BAD_REQUEST),

  ACCESS_DENIED("잘못된 권한입니다.", UNAUTHORIZED),

  /**
   * 시스템 에러
   */
  INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String description;
  private final HttpStatus status;
}
