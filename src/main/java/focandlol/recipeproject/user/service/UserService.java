package focandlol.recipeproject.user.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.user.dto.UserDto;

public interface UserService {

  UserDto getUser(CustomOauth2User user);

  void deleteUser(CustomOauth2User user);
}
