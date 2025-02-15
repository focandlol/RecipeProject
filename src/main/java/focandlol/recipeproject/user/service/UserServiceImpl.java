package focandlol.recipeproject.user.service;

import static focandlol.recipeproject.global.exception.ErrorCode.*;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.user.dto.UserDto;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDto getUser(CustomOauth2User user) {
    return UserDto.fromEntity(
        getUserEntity(user));
  }

  @Override
  public void deleteUser(CustomOauth2User user) {
    UserEntity userEntity = getUserEntity(user);

    userRepository.delete(userEntity);
  }

  private UserEntity getUserEntity(CustomOauth2User user) {
    return userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

}
