package focandlol.recipeproject.recipe.entity;

import focandlol.recipeproject.global.entity.BaseEntity;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "recipe",
    indexes = {
        @Index(name = "idx_recipe_count", columnList = "count DESC"), //좋아요 순
        @Index(name = "idx_recipe_id", columnList = "recipe_id DESC") // 최신 순
    })
public class RecipeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recipe_id")
  private Long id;

  //제목
  @Column(nullable = false, length = 40)
  private String title;

  //레시피명
  @Column(nullable = false, length = 40)
  private String name;

  //내용(요리 순서, 방법 등)
  @Column(columnDefinition = "TEXT")
  private String content;

  //추가로 하고 싶은 말
  private String bonus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserEntity user;

  //좋아요 수
  private Long count;

  public void updateRecipe(String title, String name, String content, String bonus) {
    this.title = title;
    this.name = name;
    this.content = content;
    this.bonus = bonus;
  }
}
