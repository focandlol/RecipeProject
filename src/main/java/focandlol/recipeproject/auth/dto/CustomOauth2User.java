package focandlol.recipeproject.auth.dto;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 인증 사용자 정보
 */
public class CustomOauth2User implements OAuth2User {

  private final Oauth2UserDto oauth2UserDto;

  public CustomOauth2User(Oauth2UserDto oauth2UserDto) {
    this.oauth2UserDto = oauth2UserDto;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Map.of();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2UserDto.getRole().stream()
        .map(a -> new SimpleGrantedAuthority(a))
        .collect(Collectors.toList());
  }

  @Override
  public String getName() {
    return oauth2UserDto.getName();
  }

  public String getUsername(){
    return oauth2UserDto.getUsername();
  }

  public String getEmail(){
    return oauth2UserDto.getEmail();
  }

  public Long getId(){
    return oauth2UserDto.getId();
  }
}
