package github.ikhvjs.recipes.controller;

import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.RecipeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;


@RestController
public class RecipeController {
    private static final Logger logger = LogManager.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable Long id) {
        return recipeService.findById(id)
                .map(recipe -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location(new URI("/recipe/" + recipe.getId()))
                                .body(recipe);
                    } catch (URISyntaxException e ) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/recipes")
    public Iterable<Recipe> getRecipes(){
        return recipeService.findAll();
    }

    @PostMapping("/recipe")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        logger.info("Creating new recipe with name: {}", recipe.getRecipeName());


            // Create the new recipe
            Recipe newRecipe = recipeService.save(recipe);

        try {
            // Build a created response
            return ResponseEntity
                    .created(new URI("/recipe/" + newRecipe.getId()))
                    .body(newRecipe);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/recipe/{id}")
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe recipe,
                                           @PathVariable Long id) {
        logger.info("Updating recipe with id: {}, recipe name: {}", id, recipe.getRecipeName());


        Optional<Recipe> existingRecipe = recipeService.findById(id);

        return existingRecipe.map(r -> {

            // Update the recipe
            r.setRecipeName(recipe.getRecipeName());
            r.setIsVegetarian(recipe.getIsVegetarian());
            r.setInstructions(recipe.getInstructions());
            r.setNumOfServings(recipe.getNumOfServings());
            r.setIngredients(recipe.getIngredients());

            logger.info("Updating recipe with id= " + r.getId()
                    + "===> recipeName=" + r.getRecipeName()
                    + ", isVegetarian=" + r.getIsVegetarian()
                    + ", numOfServings=" + r.getNumOfServings()
                    + ", ingredients=" + Arrays.toString(r.getIngredients().toArray())
                    + ", instructions" + r.getInstructions()
            );

            try {
                if (recipeService.update(r)) {
                    return ResponseEntity
                            .ok()
                            .body(r);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {

        logger.info("Deleting recipe with ID {}", id);

        Optional<Recipe> existingRecipe = recipeService.findById(id);

        return existingRecipe.map(p -> {
            if (recipeService.delete(p.getId())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }


}
