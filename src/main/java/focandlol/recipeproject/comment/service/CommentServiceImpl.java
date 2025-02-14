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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;

  /**
   * 댓글 추가
   */
  @Override
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
  @Override
  public ReplyCommentCreateDto.Response addReplyComment(CustomOauth2User user, Long parentId,
      ReplyCommentCreateDto.Request request) {

    UserEntity userEntity = getUser(user.getId());

    CommentEntity commentEntity = commentRepository.findByCommentIdFetch(parentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    return ReplyCommentCreateDto.Response.fromEntity(commentRepository.save(CommentEntity.builder()
        .parent(commentEntity.getParent() == null ? commentEntity : commentEntity.getParent())
        .recipe(commentEntity.getRecipe())
        /**
         * rere : 대댓글과 대대댓글 분류용
         * 대댓글 이하는 모두 2층으로 표현
         * 따라서 대댓글인지 대대댓글인지 분류 어려움
         * 대대댓글 부터는 rere에 바로 위 부모댓글 id를 넣어서 분류
         */
        .rere(commentEntity.getParent() == null ? null : commentEntity)
        .user(userEntity)
        .content(request.getContent())
        .build()));
  }

  /**
   * 해당 게시글 댓글 조회
   */
  @Override
  public Page<CommentDto> getComments(Long id, Pageable pageable) {
    Page<CommentEntity> parentCommentsPage = commentRepository.findParentComment(id, pageable);
    List<CommentEntity> parentComments = parentCommentsPage.getContent();

    // 부모 댓글 id 리스트 생성
    List<Long> parentIds = parentComments.stream()
        .map(comment -> comment.getId())
        .toList();

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

    return new PageImpl<>(new ArrayList<>(commentMap.values()), pageable, parentCommentsPage.getTotalElements());
  }

  @Override
  public CommentUpdateDto.Response updateComment(CustomOauth2User user, Long id,
      CommentUpdateDto.Request request) {

    CommentEntity commentEntity = commentRepository.findByIdFetch(id)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    validateSameUser(commentEntity, user.getId());

    commentEntity.updateComment(request.getContent());

    return CommentUpdateDto.Response.fromEntity(commentRepository.save(commentEntity));
  }

  @Override
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
        .rere(reply.getRere() == null ? null : reply.getRere().getUser().getId()) //reply.getRere() != null 대대댓글 이하란 뜻
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

