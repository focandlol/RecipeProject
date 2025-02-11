package focandlol.recipeproject.recipe.repository;

import focandlol.recipeproject.recipe.entity.RecipeTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeTagRepository extends JpaRepository<RecipeTagEntity, Long> {
  @Query("select t.name from RecipeTagEntity r "
      + "join r.tag t "
      + "where r.recipe.id = :recipeId")
  List<String> findTagNamesByRecipeId(@Param("recipeId") Long recipeId);

  @Modifying
  @Query("delete from RecipeTagEntity r "
      + "where r.recipe.id = :recipeId "
      + "and r.tag.name in :tagNames")
  void deleteRecipeTagIn(@Param("recipeId") Long recipeId,
      @Param("tagNames") List<String> tagNames);
}
