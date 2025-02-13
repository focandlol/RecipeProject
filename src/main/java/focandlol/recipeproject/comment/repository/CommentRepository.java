package focandlol.recipeproject.comment.repository;

import focandlol.recipeproject.comment.entity.CommentEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  @Query("select c from CommentEntity c join fetch c.recipe r"
      + " left join fetch c.parent p where c.id = :commentId")
  Optional<CommentEntity> findByCommentIdFetch(@Param("commentId") Long commentId);

  @Query("select c from CommentEntity c join fetch c.user u where c.parent is null and c.recipe.id = :recipeId")
  List<CommentEntity> findParentComment(@Param("recipeId") Long recipeId);

  @Query("select c from CommentEntity c " +
      "join fetch c.user " +
      "where c.parent.id in :parentIds " +
      "order by c.id")
  List<CommentEntity> findRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

  @Query("select c from CommentEntity c join fetch c.user u where c.id = :commentId")
  Optional<CommentEntity> findByIdFetch(@Param("commentId") Long commentId);

}
