package focandlol.recipeproject.user.dto;

import focandlol.recipeproject.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private Long id;

  private String name;

  private String email;

  public static UserDto fromEntity(UserEntity user) {
    return UserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }
}
