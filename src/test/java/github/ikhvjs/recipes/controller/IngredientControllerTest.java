package github.ikhvjs.recipes.controller;

import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.IngredientService;
import github.ikhvjs.recipes.service.RecipeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static github.ikhvjs.recipes.controller.JsonConverter.toJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IngredientControllerTest {

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private IngredientService ingredientService;

    @Autowired
    private MockMvc mockMvc;

    static String mockCurrentDateTimeString = "2022-08-04T10:11:30";
    static LocalDateTime mockCurrentTime = LocalDateTime.parse(mockCurrentDateTimeString);

    static MockedStatic<LocalDateTime> mockedLocalDateTime;

    @BeforeAll
    public static void setUp() {
        mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class);
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockCurrentTime);
    }

    @AfterAll
    public static void tearDown() {
        mockedLocalDateTime.close();
    }

    @Nested
    @DisplayName("GET /recipes/{id}/ingredients")
    class TestGetRecipeIngredient {
        final Long mockIngredientId1 = 1L;
        final Long mockIngredientId2 = 2L;
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockRecipeId = 1L;

        Ingredient mockIngredient1 = new Ingredient(mockIngredientId1, mockIngredientName1);
        Ingredient mockIngredient2 = new Ingredient(mockIngredientId2, mockIngredientName2);
        List<Ingredient> mockIngredients = List.of(
                mockIngredient1,
                mockIngredient2);

        @Test
        @DisplayName("return 200 ok if the retrieval succeeded")
        void testRecipeIngredientGetSuccess() throws Exception {

            doReturn(true).when(recipeService).existsById(mockRecipeId);
            doReturn(mockIngredients).when(ingredientService).findByRecipeId(mockRecipeId);

            mockMvc.perform(get("/recipes/{id}/ingredients", mockRecipeId))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))


                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(mockIngredientId1.intValue())))
                    .andExpect(jsonPath("$[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$[1].id", is(mockIngredientId2.intValue())))
                    .andExpect(jsonPath("$[1].ingredientName", is(mockIngredientName2)));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientGetNotFound() throws Exception {
            doReturn(false).when(recipeService).existsById(mockRecipeId);
            doReturn(mockIngredients).when(ingredientService).findByRecipeId(mockRecipeId);

            mockMvc.perform(get("/recipes/{id}/ingredients", mockRecipeId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the retrieval failed")
        void testRecipeIngredientGetFailure() throws Exception {
            doReturn(true).when(recipeService).existsById(mockRecipeId);
            doThrow(new RuntimeException()).when(ingredientService).findByRecipeId(mockRecipeId);

            mockMvc.perform(get("/recipes/{id}/ingredients", mockRecipeId))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /ingredients/{id}")
    class TestGetRecipeById {
        final Long mockIngredientId = 1L;

        final String mockIngredientName = "test ingredient 1";

        final Ingredient mockIngredient = new Ingredient(mockIngredientId,mockIngredientName);

        @Test
        @DisplayName("return 200 ok and the recipe if the recipe is found")
        void testGetIngredientByIdSuccess() throws Exception {


            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);

            mockMvc.perform(get("/ingredients/{id}", mockIngredientId))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(mockIngredientId.intValue())))
                    .andExpect(jsonPath("$.ingredientName", is(mockIngredientName)));
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testGetIngredientByIdNotFound() throws Exception {
            doReturn(Optional.empty()).when(ingredientService).findById(mockIngredientId);

            mockMvc.perform(get("/ingredients/{id}", mockIngredientId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if retrieval of the ingredient failed")
        void testGetIngredientFail() throws Exception {
            doThrow(new RuntimeException()).when(ingredientService).findById(mockIngredientId);


            mockMvc.perform(get("/ingredients/{id}", mockIngredientId))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /recipes/{id}/ingredients")
    class TestCreateRecipeIngredient {
        final Long mockIngredientId = 1L;
        final String mockIngredientName = "test ingredient 1";
        final Long mockRecipeId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        Ingredient mockIngredient = new Ingredient(mockIngredientId, mockIngredientName);
        Ingredient postIngredient = new Ingredient(mockIngredientName);
        List<Ingredient> mockIngredients = Collections.emptyList();

        Recipe mockRecipe = new Recipe(mockRecipeId,
                mockRecipeName,
                mockIsVegetarian,
                mockNumOfServings,
                mockInstructions,
                mockIngredients,
                mockCurrentTime);

        @Test
        @DisplayName("return 200 ok if the creation succeeded")
        void testRecipeIngredientCreateSuccess() throws Exception {
            doReturn(Optional.of(mockRecipe)).when(recipeService).findById(mockRecipeId);

            doReturn(mockIngredient).when(ingredientService).create(any());

            mockMvc.perform(post("/recipes/{id}/ingredients",mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", is(mockIngredientId.intValue())))
                    .andExpect(jsonPath("$.ingredientName", is(mockIngredientName)));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientCreateNotNull() throws Exception {
            doReturn(Optional.empty()).when(recipeService).findById(mockRecipeId);

            doReturn(mockIngredient).when(ingredientService).create(any());

            mockMvc.perform(post("/recipes/{id}/ingredients",mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 400 error if the ingredient name is null")
        void testRecipeIngredientCreateNameNull() throws Exception {
            Ingredient postIngredient = new Ingredient(null);

            doReturn(Optional.of(mockRecipe)).when(recipeService).findById(mockRecipeId);
            doReturn(mockIngredient).when(ingredientService).create(any());

            mockMvc.perform(post("/recipes/{id}/ingredients",mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("ingredientName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1/ingredients")));
        }

        @Test
        @DisplayName("return 400 error if the size of ingredient name is not between 1 and 100")
        void testRecipeIngredientCreateNameSize() throws Exception {
            Ingredient postIngredient = new Ingredient("*".repeat(101));

            doReturn(Optional.of(mockRecipe)).when(recipeService).findById(mockRecipeId);
            doReturn(mockIngredient).when(ingredientService).create(any());

            mockMvc.perform(post("/recipes/{id}/ingredients",mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("ingredientName : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1/ingredients")));
        }
        @Test
        @DisplayName("return 500 error if the creation failed")
        void testRecipeIngredientCreateFailure() throws Exception {
            doReturn(Optional.of(mockRecipe)).when(recipeService).findById(mockRecipeId);
            doThrow(new RuntimeException()).when(ingredientService).create(any());

            mockMvc.perform(post("/recipes/{id}/ingredients",mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("PATCH /ingredients/{id}")
    class TestUpdateRecipeIngredient {
        final Long mockIngredientId = 1L;
        final String mockIngredientName = "test ingredient 1";
        final Ingredient mockIngredient = new Ingredient(mockIngredientId, mockIngredientName);

        final Ingredient patchIngredient = new Ingredient(mockIngredientName);
        @Test
        @DisplayName("return 200 ok if the update succeeded")
        void testRecipeIngredientUpdateSuccess() throws Exception {
            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doReturn(mockIngredient).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(mockIngredientId.intValue())))
                    .andExpect(jsonPath("$.ingredientName", is(mockIngredientName)));
        }

        @Test
        @DisplayName("return 400 error if the ingredient name is null")
        void testRecipeIngredientUpdateRecipeNull() throws Exception {
            final Ingredient patchIngredient = new Ingredient(null);

            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doReturn(mockIngredient).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("ingredientName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/ingredients/1")));
        }

        @Test
        @DisplayName("return 400 error if the ingredient name is empty")
        void testRecipeIngredientUpdateRecipeEmpty() throws Exception {
            final Ingredient patchIngredient = new Ingredient("");

            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doReturn(mockIngredient).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("ingredientName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/ingredients/1")));
        }

        @Test
        @DisplayName("return 400 error if the size of ingredient name is not between 1 and 100")
        void testRecipeIngredientUpdateRecipeSize() throws Exception {
            final Ingredient patchIngredient = new Ingredient("*".repeat(101));

            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doReturn(mockIngredient).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("ingredientName : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/ingredients/1")));
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testRecipeIngredientUpdateRecipeNotFound() throws Exception {
            doReturn(Optional.empty()).when(ingredientService).findById(mockIngredientId);
            doReturn(mockIngredient).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the update failed")
        void testRecipeIngredientUpdateFailure() throws Exception {
            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doThrow(new RuntimeException()).when(ingredientService).update(any());

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonString(patchIngredient)))

                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /ingredients/{id}")
    class TestDeleteIngredient {

        final Long mockIngredientId = 1L;
        final String mockIngredientName = "test ingredient 1";
        final Ingredient mockIngredient = new Ingredient(mockIngredientId, mockIngredientName);


        @Test
        @DisplayName("return 200 ok if the deletion succeeded")
        void testIngredientDeleteSuccess() throws Exception {
            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doNothing().when(ingredientService).deleteById(mockIngredientId);

            mockMvc.perform(delete("/ingredients/{id}", mockIngredientId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testIngredientDeleteNotFound() throws Exception {
            doReturn(Optional.empty()).when(ingredientService).findById(mockIngredientId);

            mockMvc.perform(delete("/ingredients/{id}", mockIngredientId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the deletion failed")
        void testIngredientDeleteFailure() throws Exception {
            doReturn(Optional.of(mockIngredient)).when(ingredientService).findById(mockIngredientId);
            doThrow(new RuntimeException()).when(ingredientService).deleteById(any());

            mockMvc.perform(delete("/ingredients/{id}", mockIngredientId))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /recipes/{id}/ingredients")
    class TestDeleteRecipeIngredient {

        final Long mockRecipeId = 1L;
        @Test
        @DisplayName("return 200 ok if the deletion succeeded")
        void testRecipeIngredientsDeleteSuccess() throws Exception {
            doReturn(true).when(recipeService).existsById(mockRecipeId);
            doNothing().when(ingredientService).deleteByRecipeId(mockRecipeId);

            mockMvc.perform(delete("/recipes/{id}/ingredients", mockRecipeId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientsDeleteRecipeNotFound() throws Exception {
            doReturn(false).when(recipeService).existsById(mockRecipeId);
            doNothing().when(ingredientService).deleteByRecipeId(mockRecipeId);

            mockMvc.perform(delete("/recipes/{id}/ingredients", mockRecipeId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the deletion failed")
        void testRecipeIngredientsDeleteFailure() throws Exception {
            doReturn(true).when(recipeService).existsById(mockRecipeId);
            doThrow(new RuntimeException()).when(ingredientService).deleteByRecipeId(mockRecipeId);

            mockMvc.perform(delete("/recipes/{id}/ingredients", mockRecipeId))
                    .andExpect(status().isInternalServerError());
        }
    }


}
