package github.ikhvjs.recipes.controller;

import github.ikhvjs.recipes.exception.ResourceNotFoundException;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.RecipeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@RestController
public class RecipeController {
    private static final Logger logger = LogManager.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable Long id) throws URISyntaxException {
        Recipe existingRecipe = recipeService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Recipe with id = " + id));

        return ResponseEntity
                .ok()
                .body(existingRecipe);
    }

    @GetMapping("/recipes")
    public List<Recipe> searchRecipes(@Valid QueryString queryString){
        return recipeService.search(queryString);
    }

    @PostMapping("/recipes")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) throws URISyntaxException {
        logger.info("Creating new recipe with name: {}", recipe.getRecipeName());

        Recipe newRecipe = recipeService.create(recipe);

        return ResponseEntity
                .created(new URI("/recipes/" + newRecipe.getId()))
                .body(newRecipe);
    }

    @PatchMapping("/recipes/{id}")
    public ResponseEntity<?> updateRecipe(@Valid @RequestBody Recipe recipe,
                                           @PathVariable Long id) {
        logger.info("Updating recipe with id: {}, recipe name: {}", id, recipe.getRecipeName());

        Recipe existingRecipe = recipeService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Recipe with id = " + id));

        existingRecipe.setRecipeName(recipe.getRecipeName());
        existingRecipe.setIsVegetarian(recipe.getIsVegetarian());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setNumOfServings(recipe.getNumOfServings());

        return ResponseEntity
                .ok()
                .body(recipeService.update(recipe));
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {

        logger.info("Deleting recipe with ID {}", id);

        Recipe existingRecipe = recipeService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Recipe with id = " + id));;

        recipeService.deleteById(existingRecipe.getId());

        return ResponseEntity.ok().build();
    }


}
