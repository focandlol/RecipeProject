package focandlol.recipeproject.recipe.repository;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

  @Query("select r from RecipeEntity r join fetch r.user where r.id = :recipeId")
  Optional<RecipeEntity> findByIdFetch(@Param("recipeId") Long recipeId);

}
