package focandlol.recipeproject.like.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  //좋아요 누름
  @PostMapping("/like/{id}")
  public void addLike(@AuthenticationPrincipal CustomOauth2User user, @PathVariable long id) {
    likeService.addLike(user, id);
  }

  //좋아요 취소
  @DeleteMapping("/like/{id}")
  public void deleteLike(@AuthenticationPrincipal CustomOauth2User user, @PathVariable long id) {
    likeService.deleteLike(user, id);
  }

}
