package focandlol.recipeproject.recipe.repository;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

}
