package github.ikhvjs.recipes.annotation;

import github.ikhvjs.recipes.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueRecipeValidator implements ConstraintValidator<UniqueRecipeName, String> {
    @Autowired
    RecipeRepository recipeRepository;

    @Override
    public boolean isValid(String recipeName, ConstraintValidatorContext constraintValidatorContext) {
        return !recipeRepository.existsByRecipeName(recipeName);
    }
}
