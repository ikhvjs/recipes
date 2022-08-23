package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Recipe;

import java.util.List;
import java.util.Optional;
public interface RecipeService {
    Optional<Recipe> findById(Long id);

    Recipe save(Recipe recipe);

    List<Recipe> findAll();

    boolean update(Recipe recipe);

    boolean delete(Long id);
}
