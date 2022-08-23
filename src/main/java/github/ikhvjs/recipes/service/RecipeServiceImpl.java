package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService{
    @Override
    public Optional<Recipe> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Recipe save(Recipe recipe) {
        return null;
    }

    @Override
    public List<Recipe> findAll() {
        return null;
    }

    @Override
    public boolean update(Recipe recipe) {
        return false;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
