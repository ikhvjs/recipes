package github.ikhvjs.recipes.repository;

import github.ikhvjs.recipes.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    List<Ingredient> findByRecipeId(Long recipeId);

    @Transactional
    void deleteByRecipeId(Long recipeId);
}
