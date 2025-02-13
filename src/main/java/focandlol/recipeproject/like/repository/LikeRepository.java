package focandlol.recipeproject.like.repository;

import focandlol.recipeproject.like.entity.LikeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

  @Query("select count(l) > 0 from LikeEntity l where l.user.id = :userId and l.recipe.id = :recipeId")
  boolean existLikeByUserAndRecipe(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

  @Query("select l from LikeEntity l where l.user.id = :userId and l.recipe.id = :recipeId")
  Optional<LikeEntity> findLikeByUserAndRecipe(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
}
