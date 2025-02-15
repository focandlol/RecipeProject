package focandlol.recipeproject.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

  /**
   * /oauth2/authorization/naver 혹은 /oauth2/authorization/google 요청
   * -> 네이버 혹은 구글 로그인 -> jwt 생성 -> "/" 로 리다이렉트 -> jwt 리턴
   */
  @GetMapping("/")
  public String auth(
      @CookieValue(value = "Authorization", required = false) String authorizationCookie) {
    return authorizationCookie;
  }
}
