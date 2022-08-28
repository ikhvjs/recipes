package github.ikhvjs.recipes.specification;

import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.MessageFormat;
import java.util.List;

public final class RecipeSpecification {
    public static Specification<Recipe> isVegetarian(){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isVegetarian"),true));
    }

    public static Specification<Recipe> isNotVegetarian(){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isVegetarian"),false));
    }

    public static Specification<Recipe> numOfServings(Short numOfServings){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("numOfServings"),numOfServings));
    }

    public static Specification<Recipe> introductionsContains(String text){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("instructions"), MessageFormat.format("%{0}%", text)));
    }

    public static Specification<Recipe> includeIngredients(List<String> ingredientNames){
        return ((root, query, criteriaBuilder) -> {
            Subquery<Ingredient> subQuery = query.subquery(Ingredient.class);
            Root<Ingredient> subRoot = subQuery.from(Ingredient.class);

            Predicate sameRecipeId = criteriaBuilder.equal(subRoot.get("recipe"), root.get("id"));
            Predicate IngredientNameContains = criteriaBuilder.in(subRoot.get("ingredientName")).value(ingredientNames);
            return criteriaBuilder.exists(subQuery.select(subRoot).where(sameRecipeId, IngredientNameContains));
        });
    }
    public static Specification<Recipe> excludeIngredients(List<String> ingredientNames){
        return ((root, query, criteriaBuilder) -> {
            Subquery<Ingredient> subQuery = query.subquery(Ingredient.class);
            Root<Ingredient> subRoot = subQuery.from(Ingredient.class);

            Predicate sameRecipeId = criteriaBuilder.equal(subRoot.get("recipe"), root.get("id"));
            Predicate IngredientNameContains = criteriaBuilder.in(subRoot.get("ingredientName")).value(ingredientNames);
            return criteriaBuilder.not(criteriaBuilder.exists(subQuery.select(subRoot).where(sameRecipeId, IngredientNameContains)));
        });
    }
}
