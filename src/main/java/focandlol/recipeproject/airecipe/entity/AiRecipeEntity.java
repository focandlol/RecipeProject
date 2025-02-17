package focandlol.recipeproject.airecipe.entity;

import focandlol.recipeproject.global.entity.BaseEntity;
import focandlol.recipeproject.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ai_recipe")
public class AiRecipeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ai_recipe_id")
  private Long id;

  //레시피명
  @Column(nullable = false, length = 40)
  private String name;

  //내용(요리 순서, 방법 등)
  @Column(columnDefinition = "TEXT")
  private String content;

  //창의성
  @Column(nullable = false)
  private Double temperature;

  //추가 요청 사항
  @Column(name = "extra_details")
  private String extraDetails;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserEntity user;

  public void updateRecipe(String name, String content){
    this.name = name;
    this.content = content;
  }

}
