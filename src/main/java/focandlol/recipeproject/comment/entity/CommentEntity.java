package focandlol.recipeproject.comment.entity;

import focandlol.recipeproject.global.entity.BaseEntity;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.user.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class CommentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //내용
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RecipeEntity recipe;

  /**
   * 원댓글
   * 만약 1층(원댓글) 이면 부모가 없으므로 null
   * 2층(대댓글 이하)는 해당 댓글의 원댓글 들어감
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private CommentEntity parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentEntity> children = new ArrayList<>();

  /**
   * 댓글, 대댓글과 대대댓글 분류용
   * 원댓글은 1층, 그 이하는 모두 2층으로 분류
   * 따라서 대댓글과 대대댓글 분류 어려움
   * rere는 대대댓글부터 바로 위 부모 댓글 저장
   * rere 유무로 대댓글과 대대댓글 구분 가능
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "re_id", nullable = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private CommentEntity rere;

  public void updateComment(String content){
    this.content = content;
  }

}

