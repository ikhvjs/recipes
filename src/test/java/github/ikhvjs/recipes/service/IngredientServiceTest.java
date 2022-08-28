package github.ikhvjs.recipes.service;

import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.repository.IngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IngredientServiceTest {
    @Autowired
    private IngredientService service;

    @MockBean
    private IngredientRepository repository;

    static String mockCurrentDateTimeString = "2022-08-04T10:11:30";
    static LocalDateTime mockCurrentTime = LocalDateTime.parse(mockCurrentDateTimeString);

    @Nested
    @DisplayName("Test findById")
    class TestFindById {

        @Test
        @DisplayName("return ingredient if id is found")
        void testFindByIdSuccess() {
            final Long mockIngredientId = 1L;
            final String mockIngredientName = "test ingredient 1";
            Ingredient mockIngredient = new Ingredient(mockIngredientId, mockIngredientName);

            doReturn(Optional.of(mockIngredient)).when(repository).findById(mockIngredientId);


            Optional<Ingredient> returnedIngredient = service.findById(mockIngredientId);


            assertTrue(returnedIngredient.isPresent(), "Ingredient was not found");
            assertSame(returnedIngredient.get(), mockIngredient, "Ingredients should be the same");
        }

        @Test
        @DisplayName("return Optional.isPresent() == false if id is not found")
        void testFindByIdNotFound() {
            final Long mockIngredientId = 1L;
            doReturn(Optional.empty()).when(repository).findById(mockIngredientId);

            Optional<Ingredient> returnedIngredient = service.findById(mockIngredientId);


            assertFalse(returnedIngredient.isPresent(), "Ingredient was found, when it shouldn't be");
        }
    }

    @Nested
    @DisplayName("Test create")
    class TestCreate {

        @Test
        @DisplayName("return created ingredient")
        void testCreateSuccess() {
            final String mockIngredientName = "test ingredient 1";
            Ingredient mockIngredient = new Ingredient(null, mockIngredientName);
            Ingredient mockReturnedIngredient = new Ingredient(1L, mockIngredientName);

            doReturn(mockReturnedIngredient).when(repository).save(mockIngredient);


            Ingredient returnedIngredient = service.create(mockIngredient);

            assertNotNull(returnedIngredient,"The returned Recipe should be not null");
            assertThat(returnedIngredient).usingRecursiveComparison().isEqualTo(mockReturnedIngredient);
        }

    }

    @Nested
    @DisplayName("Test findByRecipeId")
    class TestFindByRecipeId {

        @Test
        @DisplayName("return related ingredients if recipe id is found")
        void testFindByRecipeIdSuccess() {
            final Long mockRecipeId = 1L;
            Ingredient mockIngredient1 = new Ingredient(1L, "test ingredient 1");
            Ingredient mockIngredient2 = new Ingredient(1L, "test ingredient 1");
            List<Ingredient> mockIngredients = List.of(mockIngredient1,mockIngredient2);

            doReturn(mockIngredients).when(repository).findByRecipeId(mockRecipeId);

            List<Ingredient> returnedIngredients = service.findByRecipeId(mockRecipeId);


            assertNotNull(returnedIngredients, "returned Ingredients are not null");
            assertEquals(2, returnedIngredients.size());
        }

        @Test
        @DisplayName("Return empty list if recipe id is not found")
        void testFindByRecipeIdNotFound() {
            final Long mockRecipeId = 1L;
            doReturn(Collections.emptyList()).when(repository).findByRecipeId(mockRecipeId);

            List<Ingredient> returnedIngredients = service.findByRecipeId(mockRecipeId);

            assertNotNull(returnedIngredients, "returned Ingredients are not null");
            assertEquals(0, returnedIngredients.size());
        }
    }

    @Nested
    @DisplayName("Test update")
    class TestUpdate {
        @Test
        @DisplayName("return updated ingredient")
        void testUpdateSuccess() {
            Ingredient mockIngredient = new Ingredient(1L, "test ingredient 1");

            doReturn(mockIngredient).when(repository).save(mockIngredient);

            Ingredient returnedIngredient = service.update(mockIngredient);

            assertNotNull(returnedIngredient,"The returned Ingredient should not be null");
            assertThat(returnedIngredient).usingRecursiveComparison().isEqualTo(mockIngredient);
        }

    }

    @Nested
    @DisplayName("Test deleteById")
    class TestDeleteById {
        @Test
        @DisplayName("execute repository.deleteById")
        void testDeleteByIdSuccess() {
            final Long mockIngredientId = 1L;
            doNothing().when(repository).deleteById(mockIngredientId);
            service.deleteById(mockIngredientId);
            verify(repository).deleteById(mockIngredientId);
        }
    }

    @Nested
    @DisplayName("Test deleteByRecipeId")
    class TestDeleteByRecipeId {
        @Test
        @DisplayName("execute repository.deleteByRecipeId")
        void testDeleteByRecipeIdSuccess() {
            final Long mockRecipeId = 1L;
            doNothing().when(repository).deleteByRecipeId(mockRecipeId);
            service.deleteByRecipeId(mockRecipeId);
            verify(repository).deleteByRecipeId(mockRecipeId);
        }
    }
}
