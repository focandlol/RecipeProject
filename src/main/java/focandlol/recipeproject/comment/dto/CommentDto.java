package focandlol.recipeproject.comment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

  private Long id;

  private Long userId;

  private String content;

  private List<ReplyCommentDto> children;

}
