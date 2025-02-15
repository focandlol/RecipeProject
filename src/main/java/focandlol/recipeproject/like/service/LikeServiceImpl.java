package focandlol.recipeproject.like.service;

import static focandlol.recipeproject.global.exception.ErrorCode.ALREADY_LIKE;
import static focandlol.recipeproject.global.exception.ErrorCode.NOT_ALREADY_LIKE;
import static focandlol.recipeproject.global.exception.ErrorCode.RECIPE_NOT_FOUND;
import static focandlol.recipeproject.global.exception.ErrorCode.USER_NOT_FOUND;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.like.entity.LikeEntity;
import focandlol.recipeproject.like.repository.LikeRepository;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final LikeRepository likeRepository;
  private final RedisTemplate redisTemplate;

  /**
   * update_like는 스케줄러 간격 동안 누적된 좋아요 수를 저장합니다.
   * 예를 들어, 스케줄러가 20초마다 실행된다면, update_like는 해당 20초 동안 각 레시피에서 변경된 좋아요 수를 기록합니다.
   *
   * 레시피가 100,000개 이상 존재하더라도, 20초 동안 좋아요 수가 변경된 레시피의 수는 100,000개보다 훨씬 적을 가능성이 높다고 판단했습니다.
   * 따라서, 스케줄러가 모든 레시피를 대상으로 업데이트를 실행하는 대신, 좋아요 수가 변경된 레시피만 업데이트하고,
   * 이후 update_like 데이터를 삭제하는 방식이 더 효율적이라고 생각하여 이를 구현하였습니다.
   * eg) update_like 수 0개 -> 20초간 좋아요 수 변경 2건 -
   * > lupdate_like 2개 -> 스케줄러 실행, update_like로 db에 update, update_like 삭제
   * ->  update_like 0개 반복
   * @param user
   * @param id
   */
  @Override
  public void addLike(CustomOauth2User user, Long id){
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    RecipeEntity recipeEntity = recipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    //이미 해당 게시글에 좋아요를 눌렀으면 예외
    if (likeRepository.existLikeByUserAndRecipe(userEntity.getId(), recipeEntity.getId())) {
      throw new CustomException(ALREADY_LIKE);
    }

    likeRepository.save(LikeEntity.builder()
        .recipe(recipeEntity)
        .user(userEntity)
        .build());

    redisTemplate.opsForHash().increment("update_like", String.valueOf(recipeEntity.getId()), 1);
  }

  @Override
  public void deleteLike(CustomOauth2User user, Long id){
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    RecipeEntity recipeEntity = recipeRepository.findById(id)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    //아직 해당 게시글에 좋아요를 누르지 않았다면 예외
    LikeEntity likeEntity = likeRepository.findLikeByUserAndRecipe(userEntity.getId(),
            recipeEntity.getId())
        .orElseThrow(() -> new CustomException(NOT_ALREADY_LIKE));

    likeRepository.delete(likeEntity);

    /**
     * like_add : 게시글 당 좋아요 수, 계속 쌓임
     * update_like : 해당 레시피의 scheduler 간격 사이에 변경된 좋아요 수, 매 scheduler 실행될때마다 삭제됨
     * 게시글 당 좋아요 수 db와 정합성 맞추기 위해 사용
     * LikeScheduler에서 update_like 조회해서 맞추고 삭제
     */
    redisTemplate.opsForHash().increment("like_add", String.valueOf(recipeEntity.getId()), -1);
    redisTemplate.opsForHash().increment("update_like", String.valueOf(recipeEntity.getId()), -1);
  }
}
