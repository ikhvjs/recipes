package github.ikhvjs.recipes.service;


import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RecipeServiceTest {

    @Autowired
    private RecipeService service;

    @MockBean
    private RecipeRepository repository;

    static String mockCurrentDateTimeString = "2022-08-04T10:11:30";
    static LocalDateTime mockCurrentTime = LocalDateTime.parse(mockCurrentDateTimeString);

    @Nested
    @DisplayName("Test findById")
    class TestFindById {
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";
        @Test
        @DisplayName("Success if id is found")
        void testFindByIdSuccess() {
            List<Ingredient> mockIngredients = Arrays.asList( new Ingredient(mockIngredientName1),new Ingredient(mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);
            // Setup mock repository
            doReturn(Optional.of(mockRecipe)).when(repository).findById(mockId);


            Optional<Recipe> returnedRecipe = service.findById(mockId);

            // Assert the response
            Assertions.assertTrue(returnedRecipe.isPresent(), "Recipe was not found");
            Assertions.assertSame(returnedRecipe.get(), mockRecipe, "Recipes should be the same");
        }

        @Test
        @DisplayName("Fail if id is not found")
        void testFindByIdNotFound() {
            // Setup mock repository
            doReturn(Optional.empty()).when(repository).findById(mockId);

            Optional<Recipe> returnedRecipe = service.findById(mockId);

            // Assert the response
            Assertions.assertFalse(returnedRecipe.isPresent(), "Recipe was found, when it shouldn't be");
        }

    }


}
