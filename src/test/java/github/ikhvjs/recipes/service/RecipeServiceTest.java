package github.ikhvjs.recipes.service;


import github.ikhvjs.recipes.controller.QueryString;
import github.ikhvjs.recipes.exception.InvalidSearchParamsException;
import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        private final Long mockId = 1L;

        @Test
        @DisplayName("return recipe if id is found")
        void testFindByIdSuccess() {
            final Long mockIngredientId1 = 1L;
            final Long mockIngredientId2 = 1L;
            final String mockIngredientName1 = "test ingredient 1";
            final String mockIngredientName2 = "test ingredient 2";
            final String mockRecipeName = "test ingredient 1";
            final boolean mockIsVegetarian = true;
            final Short mockNumOfServings = 2;
            final String mockInstructions = "test 1 instructions";

            List<Ingredient> mockIngredients = Arrays.asList(
                    new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);

            doReturn(Optional.of(mockRecipe)).when(repository).findById(mockId);


            Optional<Recipe> returnedRecipe = service.findById(mockId);


           assertTrue(returnedRecipe.isPresent(), "Recipe was not found");
           assertSame(returnedRecipe.get(), mockRecipe, "Recipes should be the same");
        }

        @Test
        @DisplayName("return Optional.empty() if id is not found")
        void testFindByIdNotFound() {

            doReturn(Optional.empty()).when(repository).findById(mockId);

            Optional<Recipe> returnedRecipe = service.findById(mockId);


            assertFalse(returnedRecipe.isPresent(), "Recipe was found, when it shouldn't be");
        }

    }

    @Nested
    @DisplayName("Test create")
    class TestCreate {
        private final String mockRecipeName = "test ingredient 1";
        private final boolean mockIsVegetarian = true;
        private final Short mockNumOfServings = 2;
        private final String mockInstructions = "test 1 instructions";

        Recipe mockRecipe = new Recipe(null, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, null,  null);

        Recipe mockReturnedRecipe = new Recipe(1L, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, null,  mockCurrentTime);
        @Test
        @DisplayName("return created recipe")
        void testCreateSuccess() {
            doReturn(mockReturnedRecipe).when(repository).save(mockRecipe);

            Recipe returnedRecipe = service.create(mockRecipe);

            assertNotNull(returnedRecipe,"The returned Recipe should not be null");
            assertThat(returnedRecipe).usingRecursiveComparison().isEqualTo(mockReturnedRecipe);
        }

    }

    @Nested
    @DisplayName("Test search")
    class TestSearch {
        private final String mockIsVegetarian = "true";
        private final String mockNumOfServings = "2";

        private final List<String> mockIncludeIngredients = List.of("aa","bb");

        private final List<String> mockExcludeIngredients =List.of("cc","dd");;

        private final String mockInstructionsContains = "test";

        QueryString mockValidQueryString = new QueryString(mockIsVegetarian, mockNumOfServings, mockIncludeIngredients, null, mockInstructionsContains);
        QueryString mockInvalidQueryString = new QueryString(mockIsVegetarian, mockNumOfServings, mockIncludeIngredients, mockExcludeIngredients, mockInstructionsContains);

        List<Recipe> mockRecipes = List.of(
                new Recipe(1L, "test name1", true, (short) 3,
                "mockInstructions", null,  mockCurrentTime),
                new Recipe(2L, "test name2", true,  (short) 4,
                        "mockInstructions", null,  mockCurrentTime));

        @Test
        @DisplayName("return recipes if search param is valid")
        void testCreateSuccess() {
            doReturn(mockRecipes).when(repository).findAll((Specification<Recipe>) any());


            List<Recipe> returnedRecipes = service.search(mockValidQueryString);

            assertNotNull(returnedRecipes,"The returned Recipes should not be null");
        }

        @Test
        @DisplayName("throw InvalidSearchParamsException if search param contains both includeIngredients and excludeIngredients")
        void testCreateFail() {
            doReturn(mockRecipes).when(repository).findAll((Specification<Recipe>) any());

            InvalidSearchParamsException thrown = assertThrows(
                    InvalidSearchParamsException.class,
                    () -> service.search(mockInvalidQueryString),
                    "Expected service.search() to throw, but it didn't"
            );

            assertTrue(thrown.getMessage().contains("Query String must choose either includeIngredients or excludeIngredients"));
        }
    }

    @Nested
    @DisplayName("Test update")
    class TestUpdate {

        private final Long mockRecipeId = 1L;
        private final String mockRecipeName = "test ingredient 1";
        private final boolean mockIsVegetarian = true;
        private final Short mockNumOfServings = 2;
        private final String mockInstructions = "test 1 instructions";

        Recipe mockInputRecipe = new Recipe(mockRecipeId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, null,  null);

        Recipe mockReturnedRecipe = new Recipe(mockRecipeId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, null,  mockCurrentTime);
        @Test
        @DisplayName("return updated recipe")
        void testUpdateSuccess() {
            doReturn(mockReturnedRecipe).when(repository).save(mockInputRecipe);

            Recipe returnedRecipe = service.update(mockInputRecipe);

            assertNotNull(returnedRecipe,"The returned Recipe should not be null");
            assertThat(returnedRecipe).usingRecursiveComparison().isEqualTo(mockReturnedRecipe);
        }
    }

    @Nested
    @DisplayName("Test deleteById")
    class TestDeleteById {
        @Test
        @DisplayName("execute repository.deleteById")
        void testDeleteByIdSuccess() {
           final Long mockRecipeId = 1L;
            doNothing().when(repository).deleteById(mockRecipeId);
            service.deleteById(mockRecipeId);
            verify(repository).deleteById(mockRecipeId);
        }
    }

    @Nested
    @DisplayName("Test existsById")
    class TestExistsById {
        private final Long mockRecipeId = 1L;
        @Test
        @DisplayName("return true if repository.existsById return true")
        void testExistsByIdSuccess() {
            doReturn(true).when(repository).existsById(any());

            boolean result = service.existsById(mockRecipeId);

            assertTrue(result, "result should be true");
        }

        @Test
        @DisplayName("return false if repository.existsById return false")
        void testExistsByIdFail() {
            doReturn(false).when(repository).existsById(any());

            boolean result = service.existsById(mockRecipeId);

            assertFalse(result, "result should not be true");
        }
    }

}
