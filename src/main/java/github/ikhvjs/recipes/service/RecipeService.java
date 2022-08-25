package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.controller.QueryString;

import java.util.List;
import java.util.Optional;
public interface RecipeService {
    Optional<Recipe> findById(Long id);

    Recipe create(Recipe recipe);

    List<Recipe> search(QueryString queryString);

    Recipe update(Recipe recipe);

    void deleteById(Long id);

    boolean existsById(Long id);

}
