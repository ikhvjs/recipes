package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.controller.QueryString;
import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface IngredientService {

    Optional<Ingredient> findById(Long id);

    Ingredient create(Ingredient ingredient);

    List<Ingredient> findByRecipeId(Long id);

    Ingredient update(Ingredient recipe);

    void deleteById(Long id);

    void deleteByRecipeId(Long id);
}
