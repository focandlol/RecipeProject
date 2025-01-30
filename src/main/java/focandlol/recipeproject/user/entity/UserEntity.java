package focandlol.recipeproject.user.entity;

import focandlol.recipeproject.global.entity.BaseEntity;
import focandlol.recipeproject.type.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class UserEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", nullable = false)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(length = 20, nullable = false)
  private String name;

  @Column(length = 30, nullable = false)
  private String email;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<UserType> roles;
}
