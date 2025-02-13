package focandlol.recipeproject.auth.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.like.dto.LikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

  /**
   * 유저 정보 테스트
   */
  @GetMapping("/test")
  public String myAPI(@AuthenticationPrincipal CustomOauth2User user) {
    return user.getId() + " " + user.getName() + " " + user.getUsername() + " " + user.getEmail()
        + " " + user.getAuthorities();
  }

  @GetMapping("/")
  public String auth(
      @CookieValue(value = "Authorization", required = false) String authorizationCookie) {
    return authorizationCookie;
  }
}
