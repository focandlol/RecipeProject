package focandlol.recipeproject.airecipe.repository;

import static focandlol.recipeproject.airecipe.entity.QAiRecipeEntity.*;
import static focandlol.recipeproject.airecipe.entity.QAiRecipeTagEntity.*;
import static focandlol.recipeproject.recipe.entity.QRecipeEntity.recipeEntity;
import static focandlol.recipeproject.tag.entity.QTagEntity.*;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import focandlol.recipeproject.airecipe.dto.AiRecipeSearchDto;
import focandlol.recipeproject.airecipe.entity.AiRecipeEntity;
import focandlol.recipeproject.auth.dto.CustomOauth2User;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AiRecipeQueryRepository {

  private final JPAQueryFactory query;

  public AiRecipeQueryRepository(EntityManager em) {
    this.query = new JPAQueryFactory(em);
  }

  public List<AiRecipeEntity> findAiRecipe(CustomOauth2User user, AiRecipeSearchDto aiRecipeSearchDto){
    return query
        .select(aiRecipeEntity)
        .from(aiRecipeEntity)
        .leftJoin(aiRecipeTagEntity).on(aiRecipeTagEntity.aiRecipe.eq(aiRecipeEntity))
        .leftJoin(aiRecipeTagEntity.tag, tagEntity)
        .where(
            eqUser(user.getId()),
            containTag(aiRecipeSearchDto.getTags()),
            containKeyword(aiRecipeSearchDto.getKeyword())
        )
        .groupBy(aiRecipeEntity.id)
        .having(havingCheck(aiRecipeSearchDto.getTags()))
        .orderBy(order(aiRecipeSearchDto.isUpper()))
        .fetch();
  }

  // 검색 조건 태그 모두 포함하는지(eg) 검색 조건 2개 -> 2개 모두 만족하는 것만 조회)
  private BooleanExpression havingCheck(List<String> tags) {
    if(tags == null || tags.isEmpty()){
      return null;
    }
    return aiRecipeTagEntity.count().goe((long) tags.size());
  }

  private BooleanExpression eqUser(Long userId){
    return aiRecipeEntity.user.id.eq(userId);
  }

  private BooleanExpression containTag(List<String> tags){
    if(tags == null || tags.isEmpty()) return null;
    return tagEntity.name.in(tags);
  }

  private BooleanExpression containKeyword(String keyword){
    if (keyword == null || keyword.isEmpty()) {
      return null;
    }

    return aiRecipeEntity.name.likeIgnoreCase("%" + keyword + "%")
        .or(aiRecipeEntity.content.likeIgnoreCase("%" + keyword + "%"));
  }

  private OrderSpecifier<?> order(boolean upper) {
    if(upper){
      return aiRecipeEntity.id.asc();
    }else{
      return aiRecipeEntity.id.desc();
    }
  }


}
