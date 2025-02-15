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

  @Column(nullable = false, length = 20)
  private String title;

  @Column(nullable = false, length = 20)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String content;

  private String bonus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private Long count;

  public void updateRecipe(String title, String name, String content, String bonus) {
    this.title = title;
    this.name = name;
    this.content = content;
    this.bonus = bonus;
  }
}
