package focandlol.recipeproject.like.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.like.dto.LikeDto;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final KafkaTemplate kafkaTemplate;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;

  public void addLike(CustomOauth2User user, Long id){
    kafkaTemplate.send("like-add-topic",new LikeDto(user.getId(), id));
  }

  public void deleteLike(CustomOauth2User user, Long id){
    kafkaTemplate.send("like-delete-topic",new LikeDto(user.getId(), id));
  }
}
