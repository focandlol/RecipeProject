package focandlol.recipeproject.like.service;

import static focandlol.recipeproject.global.exception.ErrorCode.ALREADY_LIKE;
import static focandlol.recipeproject.global.exception.ErrorCode.NOT_ALREADY_LIKE;
import static focandlol.recipeproject.global.exception.ErrorCode.RECIPE_NOT_FOUND;
import static focandlol.recipeproject.global.exception.ErrorCode.USER_NOT_FOUND;

import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.like.dto.LikeDto;
import focandlol.recipeproject.like.entity.LikeEntity;
import focandlol.recipeproject.like.repository.LikeRepository;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeConsumer {

  private final RedisTemplate redisTemplate;
  private final UserRepository userRepository;
  private final LikeRepository likeRepository;
  private final RecipeRepository recipeRepository;

  //좋아요 추가
  @KafkaListener(topics = "like-add-topic", groupId = "like-group")
  public void addLike(LikeDto likeDto, Acknowledgment ack){

    /**
     * validation 후 저장
     * exception catch 시 메시지 버림
     */
    try {
      UserEntity userEntity = userRepository.findById(likeDto.getUserId())
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

      RecipeEntity recipeEntity = recipeRepository.findById(likeDto.getRecipeId())
          .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

      //이미 해당 게시글에 좋아요를 눌렀으면 예외
      if (likeRepository.existLikeByUserAndRecipe(likeDto.getUserId(), likeDto.getRecipeId())) {
        throw new CustomException(ALREADY_LIKE);
      }

      likeRepository.save(LikeEntity.builder()
          .recipe(recipeEntity)
          .user(userEntity)
          .build());
    }catch(Exception e){
      ack.acknowledge();
      return;
    }

    /**
     * like_add : 게시글 당 좋아요 수, 계속 쌓임
     * update_like : 해당 레시피의 scheduler 간격 사이에 변경된 좋아요 수, 매 scheduler 실행될때마다 삭제됨
     * 게시글 당 좋아요 수 db와 정합성 맞추기 위해 사용
     * LikeScheduler에서 update_like 조회해서 맞추고 삭제
     */
    redisTemplate.opsForHash().increment("like_add", String.valueOf(likeDto.getRecipeId()), 1);
    redisTemplate.opsForHash().increment("update_like", String.valueOf(likeDto.getRecipeId()), 1);
    ack.acknowledge();
  }

  /**
   * validation 후 삭제
   * exception catch 시 메시지 버림
   */
  @KafkaListener(topics = "like-delete-topic", groupId = "like-group")
  public void deleteLike(LikeDto likeDto, Acknowledgment ack){

    try {
      UserEntity userEntity = userRepository.findById(likeDto.getUserId())
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

      RecipeEntity recipeEntity = recipeRepository.findById(likeDto.getRecipeId())
          .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

      //아직 해당 게시글에 좋아요를 누르지 않았다면 예외
      LikeEntity likeEntity = likeRepository.findLikeByUserAndRecipe(userEntity.getId(),
              recipeEntity.getId())
          .orElseThrow(() -> new CustomException(NOT_ALREADY_LIKE));

      likeRepository.delete(likeEntity);
    }catch(Exception e){
      ack.acknowledge();
      return;
    }

    /**
     * like_add : 게시글 당 좋아요 수, 계속 쌓임
     * update_like : 해당 레시피의 scheduler 간격 사이에 변경된 좋아요 수, 매 scheduler 실행될때마다 삭제됨
     * 게시글 당 좋아요 수 db와 정합성 맞추기 위해 사용
     * LikeScheduler에서 update_like 조회해서 맞추고 삭제
     */
    redisTemplate.opsForHash().increment("like_add", String.valueOf(likeDto.getRecipeId()), -1);
    redisTemplate.opsForHash().increment("update_like", String.valueOf(likeDto.getRecipeId()), -1);
    ack.acknowledge();
  }

}
