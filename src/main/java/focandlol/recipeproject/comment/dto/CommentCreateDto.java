package focandlol.recipeproject.comment.dto;


import focandlol.recipeproject.comment.entity.CommentEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentCreateDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotBlank
    @Size(max = 200)
    private String content;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response{

    private Long id;

    private String content;

    public static Response fromEntity(CommentEntity comment) {
      return Response.builder()
          .id(comment.getId())
          .content(comment.getContent())
          .build();
    }
  }
}

