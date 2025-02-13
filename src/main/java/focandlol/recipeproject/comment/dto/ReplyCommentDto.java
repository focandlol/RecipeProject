package focandlol.recipeproject.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCommentDto {

  private Long id;

  private Long userId;

  private String content;

  private Long parentId;

}
