package focandlol.recipeproject.like.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;

public interface LikeService {

  void addLike(CustomOauth2User user, Long id);

  void deleteLike(CustomOauth2User user, Long id);
}
