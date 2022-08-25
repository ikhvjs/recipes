package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService{

    private IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Optional<Ingredient> findById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> findByRecipeId(Long id) {
        return ingredientRepository.findByRecipeId(id);
    }

    @Override
    public Ingredient update(Ingredient recipe) {
        return ingredientRepository.save(recipe);
    }

    @Override
    public void deleteById(Long id) {
        ingredientRepository.deleteById(id);
    }

    @Override
    public void deleteByRecipeId(Long id) {
        ingredientRepository.deleteByRecipeId(id);
    }
}
