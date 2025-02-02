package focandlol.recipeproject.tag.entity;

import focandlol.recipeproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tag", indexes = @Index(name = "idx_tag_name", columnList = "name"))
public class TagEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private Long id;

  @Column(unique = true, nullable = false, length = 20)
  private String name;

  @Column(nullable = false)
  @ColumnDefault("0")
  private Long count;

  @PrePersist
  public void prePersist() {
    if(count == null) {
      count = 0L;
    }
  }
}
