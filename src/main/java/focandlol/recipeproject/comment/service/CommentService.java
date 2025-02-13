package focandlol.recipeproject.comment.service;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.comment.dto.CommentCreateDto;
import focandlol.recipeproject.comment.dto.CommentDto;
import focandlol.recipeproject.comment.dto.CommentUpdateDto;
import focandlol.recipeproject.comment.dto.ReplyCommentCreateDto;
import java.util.List;

public interface CommentService {

  CommentCreateDto.Response addComment(CustomOauth2User user, Long recipeId,
      CommentCreateDto.Request request);

  ReplyCommentCreateDto.Response addReplyComment(CustomOauth2User user, Long parentId,
      ReplyCommentCreateDto.Request request);

  List<CommentDto> getComments(Long id);

  CommentUpdateDto.Response updateComment(CustomOauth2User user, Long id,
      CommentUpdateDto.Request request);

  void deleteComment(CustomOauth2User user, Long id);
}
