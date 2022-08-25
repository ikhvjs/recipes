package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.controller.QueryString;
import github.ikhvjs.recipes.repository.IngredientRepository;
import github.ikhvjs.recipes.repository.RecipeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService{

    private static final Logger logger = LogManager.getLogger(RecipeServiceImpl.class);

    private  RecipeRepository recipeRepository;
    private  IngredientRepository ingredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        logger.info("Find recipe with id: {}", id);
        return recipeRepository.findById(id);

    }

    @Override
    public Recipe create(Recipe recipe) {
        logger.info("Add {}", recipe);
        return recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> search(QueryString queryString) {
        logger.info("Search recipes with query string {}",queryString);
        return recipeRepository.findAll();
    }

    @Override
    public Recipe update(Recipe recipe) {
        logger.info("Update recipe: {}", recipe);

        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Delete recipe id:{}", id);
        recipeRepository.deleteById(id);
    }

}
