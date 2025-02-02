package focandlol.recipeproject.tag.repository;

import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<TagEntity, Long> {
  List<TagEntity> findByNameIn(List<String> names);
}
