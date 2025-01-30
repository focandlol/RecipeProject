package focandlol.recipeproject.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  public final HandlerExceptionResolver handlerExceptionResolver;

  public CustomAuthenticationEntryPoint(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    /**
     * 요청 속성에서 "ex" 객체를 가져옴
     * 토큰 인증에서 예외 발생 했다면 not null
     */
    Exception exception = (Exception) request.getAttribute("ex");

    /**
     * HandlerExceptionResolver를 사용하여 예외 처리 -> 구현한 ExceptionHandler에서 security filter 에서 발생한 예외 처리
     */
    handlerExceptionResolver.resolveException(request, response, null,
        exception != null ? exception : authException);
  }
}
