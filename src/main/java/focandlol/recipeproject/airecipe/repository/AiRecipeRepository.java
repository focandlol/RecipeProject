package focandlol.recipeproject.airecipe.repository;

import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiRecipeRepository extends JpaRepository<AiRecipeEntity, Long> {

  @Query("select count(r) from AiRecipeEntity r where r.user.id = :userId")
  long countByUserId(@Param("userId") Long userId);
}
