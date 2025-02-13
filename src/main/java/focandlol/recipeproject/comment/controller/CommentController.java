package focandlol.recipeproject.comment.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.comment.dto.CommentCreateDto;
import focandlol.recipeproject.comment.dto.CommentDto;
import focandlol.recipeproject.comment.dto.CommentUpdateDto;
import focandlol.recipeproject.comment.dto.ReplyCommentCreateDto;
import focandlol.recipeproject.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/comment/{id}")
  public CommentCreateDto.Response addComment(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable(name = "id") Long recipeId
      , @RequestBody @Valid CommentCreateDto.Request request) {
    return commentService.addComment(user, recipeId, request);
  }

  @PostMapping("/replyComment/{parentId}")
  public ReplyCommentCreateDto.Response addReplyComment(
      @AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long parentId
      , @RequestBody @Valid ReplyCommentCreateDto.Request request) {

    return commentService.addReplyComment(user, parentId, request);
  }

  @GetMapping("/comment/{id}")
  public List<CommentDto> getComments(@PathVariable Long id) {
    return commentService.getComments(id);
  }

  @PutMapping("/comment/{id}")
  public CommentUpdateDto.Response updateComment(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid CommentUpdateDto.Request request) {
    return commentService.updateComment(user, id, request);
  }

  @DeleteMapping("/comment/{id}")
  public void deleteComment(@AuthenticationPrincipal CustomOauth2User user
  , @PathVariable Long id){
    commentService.deleteComment(user, id);
  }

}

