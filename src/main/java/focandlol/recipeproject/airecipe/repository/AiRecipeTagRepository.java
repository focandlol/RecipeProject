package focandlol.recipeproject.airecipe.repository;

import focandlol.recipeproject.airecipe.entity.AiRecipeTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiRecipeTagRepository extends JpaRepository<AiRecipeTagEntity, Long> {

  @Query("select t.name from AiRecipeTagEntity a "
      + "join a.tag t "
      + "where a.aiRecipe.id = :recipeId")
  List<String> findTagNamesByRecipeId(@Param("recipeId") Long recipeId);

  @Modifying
  @Query("delete from AiRecipeTagEntity a "
      + "where a.aiRecipe.id = :recipeId "
      + "and a.tag.name in :tagNames")
  void deleteRecipeTagIn(@Param("recipeId") Long recipeId,
      @Param("tagNames") List<String> tagNames);
}
