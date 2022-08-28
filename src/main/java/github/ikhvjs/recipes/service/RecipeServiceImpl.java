package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.exception.InvalidSearchParamsException;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.controller.QueryString;
import github.ikhvjs.recipes.repository.RecipeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static github.ikhvjs.recipes.specification.RecipeSpecification.*;

@Service
public class RecipeServiceImpl implements RecipeService{

    private static final Logger logger = LogManager.getLogger(RecipeServiceImpl.class);

    private RecipeRepository recipeRepository;

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
        logger.debug("Search recipes with query string {}",queryString);

        final Boolean isVegetarian = queryString.getIsVegetarian() == null ? null : Boolean.valueOf(queryString.getIsVegetarian());
        final Short numOfServings = queryString.getNumOfServings() == null ? null : Short.valueOf(queryString.getNumOfServings());
        final String instructions = queryString.getInstructionsContains();
        final List<String> includeIngredients = queryString.getIncludeIngredients();
        final List<String> excludeIngredients = queryString.getExcludeIngredients();

        List<String> messages = new ArrayList<>();

        if(includeIngredients != null && excludeIngredients != null){
            messages.add("Query String must choose either includeIngredients or excludeIngredients");
        }

        if(!messages.isEmpty()){
            throw new InvalidSearchParamsException(messages);
        }

        Specification<Recipe> specification = Specification
                .where(isVegetarian == null ? null : isVegetarian ? isVegetarian() : isNotVegetarian())
                .and(numOfServings == null ? null :numOfServings(numOfServings))
                .and(instructions == null ? null : introductionsContains(instructions))
                .and(includeIngredients == null ? null : includeIngredients(includeIngredients))
                .and(excludeIngredients == null ? null : excludeIngredients(excludeIngredients))
                ;

        return recipeRepository.findAll(specification);
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

    @Override
    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }

}
