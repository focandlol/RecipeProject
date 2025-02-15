package focandlol.recipeproject.recipe.repository;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

  @Query("select r from RecipeEntity r join fetch r.user where r.id = :recipeId")
  Optional<RecipeEntity> findByIdFetch(@Param("recipeId") Long recipeId);

  @Query("select r from RecipeEntity r join r.user u where u.id = :userId")
  Page<RecipeEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("select r from RecipeEntity r join LikeEntity l on r.id = l.recipe.id where l.user.id = :userId")
  Page<RecipeEntity> findLikeRecipeByUserId(@Param("userId") Long userId, Pageable pageable);

}
