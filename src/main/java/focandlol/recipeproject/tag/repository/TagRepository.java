package focandlol.recipeproject.tag.repository;

import focandlol.recipeproject.tag.entity.TagEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends CrudRepository<TagEntity, Long> {
  List<TagEntity> findByNameIn(List<String> names);

  Optional<TagEntity> findByName(String name);

  @Modifying
  @Query("delete from TagEntity t where t.name in :tags")
  void deleteAllByNameIn(@Param("tags") List<String> tags);
}
