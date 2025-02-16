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
   * 유튜브 댓글처럼 만들었습니다.
   * 깊이는 2로 제한했습니다.
   *
   * comment1 (원댓글, 깊이 1, parentId = null)
   *
   * comment2 (comment1 대댓글, 깊이2, parentId = comment1)
   * comment3 (comment2 대대댓글, 깊이2, parentId = comment2의 parentId = comment1)
   * ReplyCommentCreateDto.Response.fromEntity(commentRepository.save(CommentEntity.builder()
   *         .parent(commentEntity.getParent() == null ? commentEntity : commentEntity.getParent())
   * 위 코드가 깊이를 2로 제한하는 코드입니다.
   * 여기서 commentEntity는 부모 댓글입니다.
   *
   * comment2 (대댓글): 원댓글(1층)에 대한 답글 → commentEntity는 원댓글(comment1), getParent는 null → comment2.parentId = comment1
   *
   * comment3 (대대댓글): 대댓글(comment2)에 대한 답글 → commentEntity는 comment2, getParent는 not null → comment3.parentId = commentEntity.getParent() (comment1)
   *
   * 대댓글, 대대댓글 모두 parent에 원댓글을 넣어서 2층으로 제한합니다.
   *
   * 다만 대댓글, 대대댓글 모두 깊이가 2이므로 구분이 어렵습니다.
   * 이는 유튜브 처럼 대대댓글 부터는 작성 시 자동으로 부모의 id가 @부모 이름 <댓글내용> 이런 식으로 추가되는 방식으로 해결할 수 있을 것 같습니다.
   * rere 필드를 추가해서 대대댓글 부터는 부모 댓글의 id 도 저장하는 방식으로 바꾸고 조회 시 rere에는 부모 댓글의 userId도 return 하도록 변경하였습니다.
   *
   *
   * pr 답변
   * api만 제공하는 서비스일 때를 고려 하지 못했네요.
   * 일단 이번엔 깊이를 2로 정하고 유튜브처럼 만드는 것이 목적이니 현 구조를 유지하는게 좋을 것 같습니다.
   * parent id를 대댓글 이하가 모두 원댓글로 잡은 것은 깊이를 2층으로 제한했으니 조회 시 최적화가 될 것 같아서 잡았습니다. (원댓글 조회 (페이징 적용) -> 해당 원댓글을 부모로 둔 댓글 조회)
   * 지금까지는 fe 베이스를 당연하게 생각했는데 식견이 좀 넓어진 것 같습니다. 언제 기회가 되면 말씀해 주신 내용 고려하여 만들어보겠습니다.
   * 한달동안 고생 많으셨습니다.
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

    //댓글 작성자인지 확인
    validateSameUser(commentEntity, user.getId());

    commentEntity.updateComment(request.getContent());

    return CommentUpdateDto.Response.fromEntity(commentRepository.save(commentEntity));
  }

  @Override
  public void deleteComment(CustomOauth2User user, Long id) {
    CommentEntity commentEntity = commentRepository.findByIdFetch(id)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    //댓글 작성자인지 확인
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

