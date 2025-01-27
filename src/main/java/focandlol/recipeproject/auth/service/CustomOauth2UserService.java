package focandlol.recipeproject.auth.service;

import static focandlol.recipeproject.type.UserType.ROLE_USER;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.auth.dto.Oauth2UserDto;
import focandlol.recipeproject.auth.dto.response.GoogleOauth2Response;
import focandlol.recipeproject.auth.dto.response.NaverOauth2Response;
import focandlol.recipeproject.auth.dto.response.Oauth2Response;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    Oauth2Response oauth2Response = createOauth2Response(
        userRequest.getClientRegistration().getRegistrationId()
        , super.loadUser(userRequest).getAttributes());

    if (oauth2Response == null) {
      throw new OAuth2AuthenticationException("지원 x");
    }

    /**
     * 사용자 식별용(username) 생성
     */
    String username = oauth2Response.getProvider() + " " + oauth2Response.getProviderId();

    return authenticate(username, oauth2Response);
  }

  /**
   * ID에 따라 적절한 Oauth2Response 생성
   * provider마다 사용자 정보 response 형식이 달라서 분리
   */
  private Oauth2Response createOauth2Response(String registrationId,
      Map<String, Object> attributes) {
    switch (registrationId) {
      case "naver":
        return new NaverOauth2Response(attributes, registrationId);
      case "google":
        return new GoogleOauth2Response(attributes, registrationId);
      default:
        return null;
    }
  }

  /**
   * 사용자 인증: 기존 사용자 업데이트 또는 신규 사용자 등록
   */
  private OAuth2User authenticate(String username, Oauth2Response oauth2Response) {
    Optional<UserEntity> existingUser = userRepository.findByUsername(username);

    if (existingUser.isPresent()) {
      return updateUser(existingUser.get(), oauth2Response);
    } else {
      return registerNewUser(username, oauth2Response);
    }
  }

  /**
   * 기존 사용자 정보 업데이트
   */
  private OAuth2User updateUser(UserEntity userEntity, Oauth2Response oauth2Response) {
    userEntity.setName(oauth2Response.getName());
    userEntity.setEmail(oauth2Response.getEmail());
    userRepository.save(userEntity);

    return createCustomOauth2User(userEntity);
  }

  /**
   * 사용자 등록
   */
  private OAuth2User registerNewUser(String username, Oauth2Response oauth2Response) {
    UserEntity newUser = UserEntity.builder()
        .username(username)
        .email(oauth2Response.getEmail())
        .name(oauth2Response.getName())
        .roles(List.of(ROLE_USER))
        .build();

    return createCustomOauth2User(userRepository.save(newUser));
  }

  /**
   * CustomOauth2User 생성
   */
  private OAuth2User createCustomOauth2User(UserEntity userEntity) {
    return new CustomOauth2User(Oauth2UserDto.from(userEntity));
  }
}
