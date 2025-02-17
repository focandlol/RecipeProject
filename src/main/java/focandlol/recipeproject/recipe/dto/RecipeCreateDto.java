package focandlol.recipeproject.recipe.dto;

import focandlol.recipeproject.recipe.entity.RecipeEntity;
import focandlol.recipeproject.tag.entity.TagEntity;
import focandlol.recipeproject.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecipeCreateDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "RecipeCreateRequest", description = "레시피 게시글 생성 요청 dto")
  public static class Request{

    @Size(max = 40)
    @NotNull
    private String title;

    @Size(max = 40)
    @NotNull
    private String name;

    @NotNull
    List<@NotBlank String> tags;

    @NotNull
    private String content;

    @Size(max = 200)
    private String bonus;

    public RecipeEntity toEntity(UserEntity user){
      return RecipeEntity.builder()
          .title(title)
          .name(name)
          .content(content)
          .bonus(bonus)
          .user(user)
          .count(0L)
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(name = "RecipeCreateResponse", description = "레시피 게시글 생성 응답 dto")
  public static class Response{
    private Long id;

    private String title;

    private String name;

    private String content;

    private String bonus;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Response fromEntity(RecipeEntity entity, List<TagEntity> tags){
      return Response.builder()
          .id(entity.getId())
          .title(entity.getTitle())
          .name(entity.getName())
          .content(entity.getContent())
          .bonus(entity.getBonus())
          .tags(tags.stream()
              .map(tag -> tag.getName())
              .collect(Collectors.toList()))
          .createdAt(entity.getCreatedAt())
          .updatedAt(entity.getUpdatedAt())
          .build();
    }
  }
}
