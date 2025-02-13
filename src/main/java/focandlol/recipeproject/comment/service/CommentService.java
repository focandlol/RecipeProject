package focandlol.recipeproject.comment.service;

import static focandlol.recipeproject.global.exception.ErrorCode.ANOTHER_USER;
import static focandlol.recipeproject.global.exception.ErrorCode.COMMENT_NOT_FOUND;
import static focandlol.recipeproject.global.exception.ErrorCode.RECIPE_NOT_FOUND;
import static focandlol.recipeproject.global.exception.ErrorCode.USER_NOT_FOUND;

import focandlol.recipeproject.auth.dto.CustomOauth2User;
import focandlol.recipeproject.comment.dto.CommentCreateDto;
import focandlol.recipeproject.comment.dto.CommentDto;
import focandlol.recipeproject.comment.dto.CommentUpdateDto;
import focandlol.recipeproject.comment.dto.ReplyCommentCreateDto;
import focandlol.recipeproject.comment.dto.ReplyCommentDto;
import focandlol.recipeproject.comment.entity.CommentEntity;
import focandlol.recipeproject.comment.repository.CommentRepository;
import focandlol.recipeproject.global.exception.CustomException;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.recipe.repository.RecipeRepository;
import focandlol.recipeproject.user.entity.UserEntity;
import focandlol.recipeproject.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;

  /**
   * 댓글 추가
   */
  public CommentCreateDto.Response addComment(CustomOauth2User user, Long recipeId,
      CommentCreateDto.Request request) {
    UserEntity userEntity = getUser(user.getId());

    RecipeEntity recipeEntity = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new CustomException(RECIPE_NOT_FOUND));

    return CommentCreateDto.Response.fromEntity(commentRepository.save(CommentEntity.builder()
        .content(request.getContent())
        .user(userEntity)
        .recipe(recipeEntity)
        .build()));
  }

  /**
   * 대댓글 추가
   */
  public ReplyCommentCreateDto.Response addReplyComment(CustomOauth2User user, Long parentId,
      ReplyCommentCreateDto.Request request) {

    UserEntity userEntity = getUser(user.getId());

    CommentEntity commentEntity = commentRepository.findByCommentIdFetch(parentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    return ReplyCommentCreateDto.Response.fromEntity(commentRepository.save(CommentEntity.builder()
        .parent(commentEntity.getParent() == null ? commentEntity : commentEntity.getParent())
        .recipe(commentEntity.getRecipe())
        .user(userEntity)
        .content(request.getContent())
        .build()));
  }

  /**
   * 해당 게시글 댓글 조회
   */
  public List<CommentDto> getComments(Long id) {
    List<CommentEntity> parentComments = commentRepository.findParentComment(id);

    // 부모 댓글 id 리스트 생성
    List<Long> parentIds = parentComments.stream()
        .map(comment -> comment.getId())
        .collect(Collectors.toList()); // Java 16+ (toList()는 불변 리스트 반환)

    // 부모 댓글을 dto로 변환하여 map에 저장
    Map<Long, CommentDto> commentMap = parentComments.stream()
        .collect(Collectors.toMap(
            commentEntity -> commentEntity.getId(),
            comment -> toCommentDto(comment)
        ));

    // 대댓글 조회
    List<CommentEntity> replies = parentIds.isEmpty()
        ? new ArrayList<>()
        : commentRepository.findRepliesByParentIds(parentIds);


    // 대댓글을 부모 댓글의 children 리스트에 추가
    for (CommentEntity reply : replies) {
      CommentDto parentComment = commentMap.get(reply.getParent().getId());
      if (parentComment != null) {
        parentComment.getChildren().add(toReplyDto(reply));
      }
    }

    return new ArrayList<>(commentMap.values());
  }

  public CommentUpdateDto.Response updateComment(CustomOauth2User user, Long id,
      CommentUpdateDto.Request request) {

    CommentEntity commentEntity = commentRepository.findByIdFetch(id)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    validateSameUser(commentEntity, user.getId());

    commentEntity.updateComment(request.getContent());

    return CommentUpdateDto.Response.fromEntity(commentRepository.save(commentEntity));
  }

  public void deleteComment(CustomOauth2User user, Long id) {
    CommentEntity commentEntity = commentRepository.findByIdFetch(id)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    validateSameUser(commentEntity, user.getId());

    commentRepository.delete(commentEntity);
  }

  private CommentDto toCommentDto(CommentEntity comment) {
    return CommentDto.builder()
        .id(comment.getId())
        .userId(comment.getUser().getId())
        .content(comment.getContent())
        .children(new ArrayList<>())
        .build();
  }

  private ReplyCommentDto toReplyDto(CommentEntity reply) {
    return ReplyCommentDto.builder()
        .id(reply.getId())
        .userId(reply.getUser().getId())
        .content(reply.getContent())
        .parentId(reply.getParent().getId())
        .build();
  }

  private UserEntity getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  private void validateSameUser(CommentEntity commentEntity, Long userId) {
    UserEntity user = getUser(userId);

    if(!user.getId().equals(commentEntity.getUser().getId())) {
      throw new CustomException(ANOTHER_USER);
    }
  }

}

