package focandlol.recipeproject.recipe.entity;

import focandlol.recipeproject.tag.entity.TagEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "recipe_tag",
    indexes = {
        @Index(name = "idx_recipe_tag_recipe_id", columnList = "recipe_id"), //조인 시
        @Index(name = "idx_recipe_tag_tag_id", columnList = "tag_id") // 조인 시
    })
public class RecipeTagEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recipe_tag_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RecipeEntity recipe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private TagEntity tag;
}
