package github.ikhvjs.recipes.controller;

import github.ikhvjs.recipes.exception.ResourceNotFoundException;
import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.IngredientService;
import github.ikhvjs.recipes.service.RecipeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class IngredientController {
    private static final Logger logger = LogManager.getLogger(RecipeController.class);

    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/recipes/{id}/ingredients")
    public ResponseEntity<List<Ingredient>> getIngredientsByRecipeId(@PathVariable Long id) {
        logger.info("get ingredients with recipe id: {}", id);
        if (!recipeService.existsById(id)) {
            throw new ResourceNotFoundException("Not found Recipe with id = " + id);
        }

        List<Ingredient> ingredients = ingredientService.findByRecipeId(id);

        return ResponseEntity
                .ok()
                .body(ingredients);
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        logger.info("get a ingredient with recipe id: {}", id);

        Ingredient ingredient = ingredientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Ingredient with id = " + id));

        return ResponseEntity
                .ok()
                .body(ingredient);
    }

    @PostMapping("/recipes/{id}/ingredients")
    public ResponseEntity<Ingredient> createRecipeIngredient(@PathVariable Long id,
                                                 @Valid @RequestBody Ingredient ingredient) throws URISyntaxException {
        logger.info("create a ingredient {} by recipe id: {}", ingredient.getIngredientName(), id);
        Recipe recipe = recipeService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Recipe with id = " + id));

        ingredient.setRecipe(recipe);
        Ingredient newIngredient =  ingredientService.create(ingredient);

        return ResponseEntity
                .created(new URI("/ingredients/" + newIngredient.getId()))
                .body(newIngredient);
    }

    @PatchMapping("/ingredients/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable long id, @Valid @RequestBody Ingredient ingredient) {
        logger.info("update a ingredient name to {} with ingredient id: {}", ingredient.getIngredientName(), id);

        Ingredient existingIngredient = ingredientService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Ingredient with id = " + id));

        existingIngredient.setIngredientName(ingredient.getIngredientName());

        return ResponseEntity
                .ok()
                .body(ingredientService.update(existingIngredient));
    }

    @DeleteMapping("/ingredients/{id}")
    public ResponseEntity<HttpStatus> deleteIngredient(@PathVariable long id) {
        logger.info("delete a ingredient by ingredient id: {}", id);

        Ingredient existingIngredient = ingredientService
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Ingredient with id = " + id));;

        ingredientService.deleteById(existingIngredient.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/recipes/{id}/ingredients")
    public ResponseEntity<HttpStatus> deleteRecipeIngredients(@PathVariable Long id)  {
        logger.info("delete all ingredients by recipe id: {}", id);

        if (!recipeService.existsById(id)) {
            throw new ResourceNotFoundException("Not found Recipe with id = " + id);
        }

        ingredientService.deleteByRecipeId(id);

        return ResponseEntity.ok().build();
    }
}
