package focandlol.recipeproject.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER = "Bearer ";

  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = getToken(request);
      if (token != null) {
        Authentication authentication = jwtProvider.authentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      /**
       * 토큰 인증 과정에서 발생한 예외 catch
       * 요청 속성에 예외 객체 저장
       * 이후 CustomAuthenticationEntryPoint에서 예외 처리
       */
      request.setAttribute("ex", e);
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 요청 헤더에서 jwt 추출
   */
  private String getToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
      return null;
    }
    return authorizationHeader.substring(BEARER.length());
  }
}