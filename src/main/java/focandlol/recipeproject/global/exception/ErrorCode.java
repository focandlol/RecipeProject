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
  INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  INVALID_TAG("존재하지 않는 태그입니다.",BAD_REQUEST),
  EXIST_TAG("이미 존재하는 태그입니다",BAD_REQUEST),

  USER_NOT_FOUND("존재하지 않는 유저입니다",BAD_REQUEST),

  TOO_MANY_RECIPE("AI 레시피는 10개까지만 생성 가능합니다.",BAD_REQUEST),

  FAILED_ROLLBACK("롤백 실패",HttpStatus.INTERNAL_SERVER_ERROR),

  FAILED_SAVE_TAG("태그 저장 실패",HttpStatus.INTERNAL_SERVER_ERROR),

  AI_RECIPE_NOT_FOUND("존재하지 않는 레시피입니다",BAD_REQUEST),
  RECIPE_NOT_FOUND("존재하지 않는 레시피입니다",BAD_REQUEST),

  FAILED_LIKE("좋아요 실패",HttpStatus.INTERNAL_SERVER_ERROR),
  ALREADY_LIKE("이미 좋아요를 눌렀습니다.",BAD_REQUEST),
  NOT_ALREADY_LIKE("좋아요를 누른 레시피가 아닙니다.",BAD_REQUEST),

  ANOTHER_USER("작성자가 다릅니다.",BAD_REQUEST),

  COMMENT_NOT_FOUND("해당 코멘트가 없습니다", BAD_REQUEST);

  private final String description;
  private final HttpStatus status;
}
