package focandlol.recipeproject.comment.controller;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.comment.dto.CommentCreateDto;
import focandlol.recipeproject.comment.dto.CommentDto;
import focandlol.recipeproject.comment.dto.CommentUpdateDto;
import focandlol.recipeproject.comment.dto.ReplyCommentCreateDto;
import focandlol.recipeproject.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  /**
   * 원댓글 작성
   * @param user
   * @param recipeId
   * @param request
   * @return
   */
  @PostMapping("/comment/{id}")
  public CommentCreateDto.Response addComment(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable(name = "id") Long recipeId
      , @RequestBody @Valid CommentCreateDto.Request request) {
    return commentService.addComment(user, recipeId, request);
  }

  /**
   * 대댓글 이하 작성
   * @param user
   * @param parentId : 댓글, 대댓글, 대대댓글 등 다 올 수 있음
   * @param request
   * @return
   */
  @PostMapping("/replyComment/{parentId}")
  public ReplyCommentCreateDto.Response addReplyComment(
      @AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long parentId
      , @RequestBody @Valid ReplyCommentCreateDto.Request request) {

    return commentService.addReplyComment(user, parentId, request);
  }

  /**
   * 해당 레시피 게시글 댓글 조회
   */
  @GetMapping("/comment/{id}")
  public Page<CommentDto> getComments(@PathVariable Long id, Pageable pageable) {
    return commentService.getComments(id, pageable);
  }

  /**
   * 댓글 수정
   */
  @PutMapping("/comment/{id}")
  public CommentUpdateDto.Response updateComment(@AuthenticationPrincipal CustomOauth2User user
      , @PathVariable Long id
      , @RequestBody @Valid CommentUpdateDto.Request request) {
    return commentService.updateComment(user, id, request);
  }

  /**
   * 댓글 삭제
   */
  @DeleteMapping("/comment/{id}")
  public void deleteComment(@AuthenticationPrincipal CustomOauth2User user
  , @PathVariable Long id){
    commentService.deleteComment(user, id);
  }

}

