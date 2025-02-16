package focandlol.recipeproject.recipe.repository;

import static focandlol.recipeproject.recipe.entity.QRecipeEntity.*;
import static focandlol.recipeproject.recipe.entity.QRecipeTagEntity.*;
import static focandlol.recipeproject.tag.entity.QTagEntity.tagEntity;
import static focandlol.recipeproject.type.RecipeSortType.*;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import focandlol.recipeproject.recipe.dto.RecipeSearchDto;
import focandlol.recipeproject.recipe.entity.RecipeEntity;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class QueryRecipeRepository {

  private final JPAQueryFactory query;

  public QueryRecipeRepository(EntityManager em) {
    this.query = new JPAQueryFactory(em);
  }

  public Page<RecipeEntity> findRecipes(
      RecipeSearchDto searchDto, Pageable pageable) {
    List<Long> ids = query.selectDistinct(recipeEntity.id)
        .from(recipeEntity)
        .leftJoin(recipeTagEntity).on(recipeEntity.id.eq(recipeTagEntity.recipe.id))
        .leftJoin(tagEntity).on(recipeTagEntity.tag.id.eq(tagEntity.id))
        .where(containTag(searchDto.getTags()), containKeyword(searchDto.getKeyword()))
        .groupBy(recipeEntity.id)
        .having(havingCheck(searchDto.getTags()))
        .fetch();

    List<RecipeEntity> content = query
        .select(recipeEntity)
        .from(recipeEntity)
        .where(recipeEntity.id.in(ids))
        .orderBy(order(searchDto).toArray(new OrderSpecifier[0]))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return PageableExecutionUtils.getPage(content, pageable, () -> ids.size());
  }

  // 검색 조건 태그 모두 포함하는지(eg) 검색 조건 2개 -> 2개 모두 만족하는 것만 조회)
  private BooleanExpression havingCheck(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return null;
    }
    return recipeTagEntity.tag.count().goe((long) tags.size());
  }

  //태그 포함하는지
  private BooleanExpression containTag(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return null;
    }
    return tagEntity.name.in(tags);
  }

  //키워드 검색 조건
  //제목, 레시피명, 내용
  private BooleanExpression containKeyword(String keyword) {
    if (keyword == null || keyword.isEmpty()) {
      return null;
    }

    return recipeEntity.name.likeIgnoreCase("%" + keyword + "%")
        .or(recipeEntity.title.likeIgnoreCase("%" + keyword + "%"))
        .or(recipeEntity.content.likeIgnoreCase("%" + keyword + "%"));
  }

  //정렬 조건
  //좋아요 순, 최신 순
  private List<OrderSpecifier<?>> order(RecipeSearchDto request) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    Order order = request.isUpper() ? Order.ASC : Order.DESC;

    if (request.getSortBy() != null && request.getSortBy() == LIKES) {
      orderSpecifiers.add(new OrderSpecifier<>(order, recipeEntity.count));
      orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, recipeEntity.id));
    } else {
      orderSpecifiers.add(new OrderSpecifier<>(order, recipeEntity.id));
    }
    return orderSpecifiers;
  }
}
