package focandlol.recipeproject.auth.dto;

import focandlol.recipeproject.type.UserType;
import focandlol.recipeproject.user.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2UserDto {

  private Long id;
  private String username;
  private String name;
  private String email;
  private List<String> roles;

  public static Oauth2UserDto from(UserEntity user) {
    return Oauth2UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .email(user.getEmail())
        .roles(user.getRoles().stream().map(a -> a.toString()).collect(Collectors.toList()))
        .build();
  }

}
