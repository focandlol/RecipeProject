package focandlol.recipeproject.auth.jwt;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.auth.dto.Oauth2UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

  private final SecretKey secretKey;
  private final Long tokenValidTime;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret,
      @Value("${spring.jwt.time}") Long tokenTime) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    tokenValidTime = tokenTime;
  }

  /**
   * jwt 토큰 검증, UsernamePasswordAuthenticationToken 생성
   */
  public Authentication authentication(String token) {
    Jws<Claims> jws = verifyToken(token);

    Oauth2UserDto oauth2UserDto = Oauth2UserDto.builder()
        .id(getId(jws))
        .username(getUsername(jws))
        .name(getName(jws))
        .email(getEmail(jws))
        .roles(getRoles(jws))
        .build();

    CustomOauth2User customOauth2User = new CustomOauth2User(oauth2UserDto);

    return new UsernamePasswordAuthenticationToken(customOauth2User, null,
        customOauth2User.getAuthorities());
  }

  /**
   * 토큰 생성
   */
  public String createJwt(CustomOauth2User user, List<String> roles) {

    return Jwts.builder()
        .claim("id", user.getId())
        .claim("username", user.getUsername())
        .claim("name", user.getName())
        .claim("email", user.getEmail())
        .claim("roles", roles)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + tokenValidTime))
        .signWith(secretKey)
        .compact();
  }

  /**
   * 토큰 검증, signature 유효하면 claim 정보 반환
   */
  public Jws<Claims> verifyToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }

  public Long getId(Jws<Claims> jws) {
    return jws.getPayload().get("id", Long.class);
  }

  public String getUsername(Jws<Claims> jws) {
    return jws.getPayload().get("username", String.class);
  }

  public String getName(Jws<Claims> jws) {
    return jws.getPayload().get("name", String.class);
  }

  public String getEmail(Jws<Claims> jws) {
    return jws.getPayload().get("email", String.class);
  }

  public List<String> getRoles(Jws<Claims> jws) {
    return jws.getPayload().get("roles", List.class);
  }
}
