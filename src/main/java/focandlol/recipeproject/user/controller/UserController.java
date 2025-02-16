package focandlol.recipeproject.user.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.user.dto.UserDto;
import focandlol.recipeproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * 본인 정보 조회
   */
  @GetMapping("user")
  public UserDto getUser(@AuthenticationPrincipal CustomOauth2User user) {
    return userService.getUser(user);
  }

  /**
   * 탈퇴
   */
  @DeleteMapping("user")
  public void deleteUser(@AuthenticationPrincipal CustomOauth2User user) {
    userService.deleteUser(user);
  }
}
