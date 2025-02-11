package focandlol.recipeproject.recipe.repository;

import static focandlol.recipeproject.recipe.entity.QRecipeEntity.*;
import static focandlol.recipeproject.recipe.entity.QRecipeTagEntity.*;
import static focandlol.recipeproject.tag.entity.QTagEntity.tagEntity;
import static focandlol.recipeproject.type.RecipeSearchType.*;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class QueryRecipeRepository {

  private final JPAQueryFactory query;

  public QueryRecipeRepository(EntityManager em) {
    this.query = new JPAQueryFactory(em);
  }

  public Page<RecipeEntity> findRecipes(
      RecipeSearchDto searchDto, Pageable pageable){
    List<RecipeEntity> content = query
        .select(recipeEntity)
        .from(recipeEntity)
        .leftJoin(recipeTagEntity).on(recipeTagEntity.recipe.eq(recipeEntity))
        .leftJoin(recipeTagEntity.tag, tagEntity)
        .where(
            containTag(searchDto.getTags()),
            containKeyword(searchDto.getKeyword())
        )
        .groupBy(recipeEntity.id)
        .having(havingCheck(searchDto.getTags()))
        .orderBy(order(searchDto))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long count = query
        .select(recipeEntity.id)
        .from(recipeEntity)
        .leftJoin(recipeTagEntity).on(recipeTagEntity.recipe.eq(recipeEntity))
        .leftJoin(recipeTagEntity.tag, tagEntity)
        .where(
            containTag(searchDto.getTags()),
            containKeyword(searchDto.getKeyword())
        )
        .groupBy(recipeEntity.id)
        .having(havingCheck(searchDto.getTags()))
        .fetch()
        .size();

    return new PageImpl<>(content, pageable, count);
  }

  //태그 전부 포함하는지
  private BooleanExpression havingCheck(List<String> tags) {
    if(tags == null || tags.isEmpty()){
      return null;
    }
    return recipeTagEntity.count().gt((long) tags.size());
  }

  //태그 포함하는지
  private BooleanExpression containTag(List<String> tags){
    if(tags == null || tags.isEmpty()) return null;
    return tagEntity.name.in(tags);
  }

  //키워드 검색 조건
  //제목, 레시피명, 내용
  private BooleanExpression containKeyword(String keyword){
    if(keyword == null || keyword.isEmpty()) return null;

    BooleanExpression nameCondition = recipeEntity.name.containsIgnoreCase(keyword);
    BooleanExpression titleCondition = recipeEntity.title.containsIgnoreCase(keyword);
    BooleanExpression contentCondition = recipeEntity.content.containsIgnoreCase(keyword);

    return nameCondition.or(titleCondition).or(contentCondition);
  }

  //정렬 조건
  //좋아요 순, 최신 순
  private OrderSpecifier<?> order(RecipeSearchDto request) {
    Order order = request.isUpper() ? Order.ASC : Order.DESC;

    if(request.getSortBy() != null && request.getSortBy() == LIKES){
      return new OrderSpecifier<>(order, recipeEntity.count);
    }else{
      return new OrderSpecifier<>(order, recipeEntity.id);
    }
  }
}
