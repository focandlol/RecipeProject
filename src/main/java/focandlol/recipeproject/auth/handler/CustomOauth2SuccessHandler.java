package focandlol.recipeproject.auth.handler;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.auth.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;

  /**
   * Oauth2 인증 성공 시 호출
   * 인증된 사용자 정보로 jwt 생성
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomOauth2User customUserDetails = (CustomOauth2User) authentication.getPrincipal();

    /**
     * jwt 생성
     */
    String token = createToken(customUserDetails);

    /**
     * "/" 로 jwt 쿠키에 담아서 리다이렉트
     */
    Cookie jwtCookie = new Cookie("Authorization", token);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60);
    response.addCookie(jwtCookie);


    response.sendRedirect("/");
  }

  private String createToken(CustomOauth2User user) {
    /**
     * 사용자 권한 문자열 리스트로 변환
     */
    List<String> roles = user.getAuthorities().stream()
        .map(a -> a.getAuthority())
        .collect(Collectors.toList());

    return jwtProvider.createJwt(user, roles);
  }
}
