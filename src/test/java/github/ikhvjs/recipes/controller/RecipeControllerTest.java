package github.ikhvjs.recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.RecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ValidationException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {

    static String mockCurrentDateTimeString = "2022-08-04T10:11:30";
    static LocalDateTime mockCurrentTime = LocalDateTime.parse(mockCurrentDateTimeString);
    @MockBean
    private RecipeService service;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @DisplayName("GET /recipe/{id}")
    class TestGetRecipeById {

        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        @Test
        @DisplayName("return 200 ok and the recipe if the recipe is found")
        void testGetRecipeByIdFound() throws Exception {

            List<Ingredient> mockIngredients = Arrays.asList( new Ingredient(mockIngredientName1),new Ingredient(mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);
            // Setup mocked service
            doReturn(Optional.of(mockRecipe)).when(service).findById(mockId);

            mockMvc.perform(get("/recipe/{id}", mockId))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(header().string(HttpHeaders.LOCATION, "/recipe/" + mockId))

                    .andExpect(jsonPath("$.id", is(mockId.intValue())))
                    .andExpect(jsonPath("$.recipeName", is(mockRecipeName)))
                    .andExpect(jsonPath("$.isVegetarian", is(mockIsVegetarian)))
                    .andExpect(jsonPath("$.numOfServings", is(mockNumOfServings.intValue())))
                    .andExpect(jsonPath("$.instructions", is(mockInstructions)))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$.ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$.modifiedTime", is(mockCurrentDateTimeString)));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testGetRecipeByIdNotFound() throws Exception {
            // Setup mocked service
            doReturn(Optional.empty()).when(service).findById(1L);

            mockMvc.perform(get("/recipe/{id}", 1))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /recipe")
    class TestCreateRecipe {
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        @Test
        @DisplayName("return 200 ok and the recipe if the creation is successful")
        void testCreateRecipeSuccess() throws Exception {

            List<Ingredient> postIngredients = Arrays.asList( new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));
            Recipe postRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            List<Ingredient> mockIngredients = Arrays.asList(
                    new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));

            Recipe mockRecipe = new Recipe(mockId,
                    mockRecipeName,
                    mockIsVegetarian,
                    mockNumOfServings,
                    mockInstructions,
                    mockIngredients,
                    mockCurrentTime);
            // Setup mocked service
            doReturn(mockRecipe).when(service).save(any());

            mockMvc.perform(post("/recipe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(header().string(HttpHeaders.LOCATION, "/recipe/"+mockId))

                    .andExpect(jsonPath("$.id", is(mockId.intValue())))
                    .andExpect(jsonPath("$.recipeName", is(mockRecipeName)))
                    .andExpect(jsonPath("$.isVegetarian", is(mockIsVegetarian)))
                    .andExpect(jsonPath("$.numOfServings", is(mockNumOfServings.intValue())))
                    .andExpect(jsonPath("$.instructions", is(mockInstructions)))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$.ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$.modifiedTime", is(mockCurrentDateTimeString)));
        }

        @Test
        @DisplayName("return 500 Error if the creation is not executed successfully")
        void testCreateRecipeBadRequest() throws Exception {

            List<Ingredient> postIngredients = Arrays.asList( new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));
            Recipe postRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            // Setup mocked service
            doReturn(false).when(service).save(any());

            mockMvc.perform(post("/recipe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJsonString(postRecipe)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /recipes")
    class TestGetRecipes {
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final String mockIngredientName3 = "test ingredient 3";
        final Long mockId1 = 1L;
        final String mockRecipeName1 = "test ingredient 1";
        final boolean mockIsVegetarian1 = true;
        final Short mockNumOfServings1 = 2;
        final String mockInstructions1 = "test 1 instructions";
        final Long mockId2 = 2L;
        final String mockRecipeName2 = "test ingredient 2";
        final boolean mockIsVegetarian2 = true;
        final Short mockNumOfServings2 = 3;
        final String mockInstructions2 = "test 2 instructions";

        @Test
        @DisplayName("return 200 ok and the recipes if there is any recipe")
        void testGetRecipesSuccess() throws Exception {
            List<Ingredient> mockIngredients1 = Arrays.asList(
                    new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));

            Recipe mockRecipe1 = new Recipe(mockId1,
                    mockRecipeName1,
                    mockIsVegetarian1,
                    mockNumOfServings1,
                    mockInstructions1,
                    mockIngredients1,
                    mockCurrentTime);

            List<Ingredient> mockIngredients2 = Arrays.asList(
                    new Ingredient(mockIngredientName3)
            );

            Recipe mockRecipe2 = new Recipe(mockId2,
                    mockRecipeName2,
                    mockIsVegetarian2,
                    mockNumOfServings2,
                    mockInstructions2,
                    mockIngredients2,
                    mockCurrentTime);

            List<Recipe> recipes = Arrays.asList(mockRecipe1,mockRecipe2);

            // Setup mocked service
            doReturn(recipes).when(service).findAll();

            mockMvc.perform(get("/recipes"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(mockId1.intValue())))
                    .andExpect(jsonPath("$[0].recipeName", is(mockRecipeName1)))
                    .andExpect(jsonPath("$[0].isVegetarian", is(mockIsVegetarian1)))
                    .andExpect(jsonPath("$[0].numOfServings", is(mockNumOfServings1.intValue())))
                    .andExpect(jsonPath("$[0].instructions", is(mockInstructions1)))
                    .andExpect(jsonPath("$[0].ingredients", hasSize(2)))
                    .andExpect(jsonPath("$[0].ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$[0].ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$[0].modifiedTime", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$[1].id", is(mockId2.intValue())))
                    .andExpect(jsonPath("$[1].recipeName", is(mockRecipeName2)))
                    .andExpect(jsonPath("$[1].isVegetarian", is(mockIsVegetarian2)))
                    .andExpect(jsonPath("$[1].numOfServings", is(mockNumOfServings2.intValue())))
                    .andExpect(jsonPath("$[1].instructions", is(mockInstructions2)))
                    .andExpect(jsonPath("$[1].ingredients", hasSize(1)))
                    .andExpect(jsonPath("$[1].ingredients[0].ingredientName", is(mockIngredientName3)))
                    .andExpect(jsonPath("$[1].modifiedTime", is(mockCurrentDateTimeString)));
        }

        @Test
        @DisplayName("return 200 ok and an empty array if there is no recipe")
        void testGetRecipesEmpty() throws Exception {
            // Setup mocked service
            doReturn(Collections.emptyList()).when(service).findAll();

            mockMvc.perform(get("/recipes"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PATCH /recipe/{id}")
    class TestUpdateRecipe {
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        @Test
        @DisplayName("return 200 ok and the updated recipes if update successfully")
        void testUpdateRecipesSuccess() throws Exception {
            List<Ingredient> patchIngredients = Arrays.asList(
                    new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));
            Recipe patchRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, patchIngredients);

            List<Ingredient> mockIngredients = Arrays.asList(
                    new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);


            // Setup mocked service
            doReturn(Optional.of(mockRecipe)).when(service).findById(mockId);
            doReturn(true).when(service).update(any());

            mockMvc.perform(patch("/recipe/{id}", mockId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(mockId.intValue())))
                    .andExpect(jsonPath("$.recipeName", is(mockRecipeName)))
                    .andExpect(jsonPath("$.isVegetarian", is(mockIsVegetarian)))
                    .andExpect(jsonPath("$.numOfServings", is(mockNumOfServings.intValue())))
                    .andExpect(jsonPath("$.instructions", is(mockInstructions)))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$.ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$.modifiedTime", is(mockCurrentDateTimeString)));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipePatchNotFound() throws Exception {

            List<Ingredient> patchIngredients = Arrays.asList(
                    new Ingredient(mockIngredientName1),
                    new Ingredient(mockIngredientName2));
            Recipe patchRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, patchIngredients);
            // Setup mocked service
            doReturn(Optional.empty()).when(service).findById(mockId);

            mockMvc.perform(patch("/recipe/{id}", mockId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /recipe/{id}")
    class TestDeleteRecipe {
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        @Test
        @DisplayName("return 200 ok if the recipe is deleted successfully")
        void testRecipeDeleteSuccess() throws Exception {
            List<Ingredient> mockIngredients = Arrays.asList( new Ingredient(mockIngredientName1),new Ingredient(mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);

            // Setup the mocked service
            doReturn(Optional.of(mockRecipe)).when(service).findById(mockId);
            doReturn(true).when(service).delete(mockId);

            mockMvc.perform(delete("/recipe/{id}", mockId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeDeleteNotFound() throws Exception {
            // Setup the mocked service
            doReturn(Optional.empty()).when(service).findById(mockId);

            // Execute our DELETE request
            mockMvc.perform(delete("/recipe/{id}", mockId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the deletion fail")
        void testProductDeleteFailure() throws Exception {
            List<Ingredient> mockIngredients = Arrays.asList( new Ingredient(mockIngredientName1),new Ingredient(mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);

            // Setup the mocked service
            doReturn(Optional.of(mockRecipe)).when(service).findById(mockId);
            doReturn(false).when(service).delete(mockId);

            // Execute our DELETE request
            mockMvc.perform(delete("/recipe/{id}", mockId))
                    .andExpect(status().isInternalServerError());
        }
    }

    static String toJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
