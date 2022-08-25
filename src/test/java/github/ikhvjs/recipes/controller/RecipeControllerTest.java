package github.ikhvjs.recipes.controller;

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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest extends JsonConverter {

    static String mockCurrentDateTimeString = "2022-08-04T10:11:30";
    static LocalDateTime mockCurrentTime = LocalDateTime.parse(mockCurrentDateTimeString);
    @MockBean
    private RecipeService service;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @DisplayName("GET /recipes/{id}")
    class TestGetRecipeById {
        final Long mockIngredientId1 = 1L;

        final String mockIngredientName1 = "test ingredient 1";
        final Long mockIngredientId2 = 1L;

        final String mockIngredientName2 = "test ingredient 2";
        final Long mockRecipeId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        @Test
        @DisplayName("return 200 ok and the recipe if the recipe is found")
        void testGetRecipeByIdSuccess() throws Exception {
            List<Ingredient> mockIngredients = List.of(
                    new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe mockRecipe = new Recipe(mockRecipeId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                    mockInstructions, mockIngredients,  mockCurrentTime);

            doReturn(Optional.of(mockRecipe)).when(service).findById(mockRecipeId);

            mockMvc.perform(get("/recipes/{id}", mockRecipeId))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(header().string(HttpHeaders.LOCATION, "/recipes/" + mockRecipeId))

                    .andExpect(jsonPath("$.id", is(mockRecipeId.intValue())))
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
            doReturn(Optional.empty()).when(service).findById(mockRecipeId);

            mockMvc.perform(get("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if retrieval of recipe failed")
        void testGetRecipeFail() throws Exception {
            doThrow(new RuntimeException()).when(service).findById(mockRecipeId);


            mockMvc.perform(get("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /recipes")
    class TestCreateRecipe {
        final Long mockIngredientId1 = 1L;
        final Long mockIngredientId2 = 2L;
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockRecipeId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));
        Recipe postRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                mockNumOfServings, mockInstructions, postIngredients);

        List<Ingredient> mockIngredients = List.of(
                new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));

        Recipe mockRecipe = new Recipe(mockRecipeId,
                mockRecipeName,
                mockIsVegetarian,
                mockNumOfServings,
                mockInstructions,
                mockIngredients,
                mockCurrentTime);

        @Test
        @DisplayName("return 200 ok and the recipe if the creation succeeded")
        void testCreateRecipeSuccess() throws Exception {
            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(header().string(HttpHeaders.LOCATION, "/recipes/"+ mockRecipeId))

                    .andExpect(jsonPath("$.id", is(mockRecipeId.intValue())))
                    .andExpect(jsonPath("$.recipeName", is(mockRecipeName)))
                    .andExpect(jsonPath("$.isVegetarian", is(mockIsVegetarian)))
                    .andExpect(jsonPath("$.numOfServings", is(mockNumOfServings.intValue())))
                    .andExpect(jsonPath("$.instructions", is(mockInstructions)))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].id", is(mockIngredientId1.intValue())))
                    .andExpect(jsonPath("$.ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$.ingredients[1].id", is(mockIngredientId2.intValue())))
                    .andExpect(jsonPath("$.ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$.modifiedTime", is(mockCurrentDateTimeString)));
        }

        @Test
        @DisplayName("return 500 Error if the creation failed")
        void testCreateRecipeBadRequest() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());
            doThrow(new RuntimeException()).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /recipes")
    class TestGetRecipes {
        final Long mockIngredientId1 = 1L;
        final Long mockIngredientId2 = 2L;
        final Long mockIngredientId3 = 3L;
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final String mockIngredientName3 = "test ingredient 3";
        final Long mockRecipeId1 = 1L;
        final String mockRecipeName1 = "test ingredient 1";
        final boolean mockIsVegetarian1 = true;
        final Short mockNumOfServings1 = 2;
        final String mockInstructions1 = "test 1 instructions";
        final Long mockRecipeId2 = 2L;
        final String mockRecipeName2 = "test ingredient 2";
        final boolean mockIsVegetarian2 = true;
        final Short mockNumOfServings2 = 3;
        final String mockInstructions2 = "test 2 instructions";

        List<Ingredient> mockIngredients1 = List.of(
                new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));

        Recipe mockRecipe1 = new Recipe(mockRecipeId1,
                mockRecipeName1,
                mockIsVegetarian1,
                mockNumOfServings1,
                mockInstructions1,
                mockIngredients1,
                mockCurrentTime);

        List<Ingredient> mockIngredients2 = List.of(
                new Ingredient(mockIngredientId3, mockIngredientName3)
        );

        Recipe mockRecipe2 = new Recipe(mockRecipeId2,
                mockRecipeName2,
                mockIsVegetarian2,
                mockNumOfServings2,
                mockInstructions2,
                mockIngredients2,
                mockCurrentTime);

        List<Recipe> mockRecipes = List.of(mockRecipe1,mockRecipe2);


        @Test
        @DisplayName("return 200 ok and the recipes if there is any recipe")
        void testGetRecipesSuccess() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(mockRecipeId1.intValue())))
                    .andExpect(jsonPath("$[0].recipeName", is(mockRecipeName1)))
                    .andExpect(jsonPath("$[0].isVegetarian", is(mockIsVegetarian1)))
                    .andExpect(jsonPath("$[0].numOfServings", is(mockNumOfServings1.intValue())))
                    .andExpect(jsonPath("$[0].instructions", is(mockInstructions1)))
                    .andExpect(jsonPath("$[0].ingredients", hasSize(2)))
                    .andExpect(jsonPath("$[0].ingredients[0].ingredientName", is(mockIngredientName1)))
                    .andExpect(jsonPath("$[0].ingredients[1].ingredientName", is(mockIngredientName2)))
                    .andExpect(jsonPath("$[0].modifiedTime", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$[1].id", is(mockRecipeId2.intValue())))
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
            doReturn(Collections.emptyList()).when(service).search(any());

            mockMvc.perform(get("/recipes"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("return 200 ok if isVegetarian is set")
        void testSearchRecipeIsVegetarian() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?isVegetarian="+mockIsVegetarian1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 200 ok if numOfServings is set")
        void testSearchRecipeNumOfServings() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?numOfServings="+mockNumOfServings1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 200 ok if includedIngredients is set")
        void testSearchRecipeIncludeIngredients() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?includedIngredients="+mockIngredientName1+","+mockIngredientName2))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 200 ok if excludedIngredients is set")
        void testSearchRecipeExcludeIngredients() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?excludedIngredients="+mockIngredientName1+","+mockIngredientName2))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 200 ok if instructionsContain is set")
        void testSearchRecipeInstructionsContain() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?instructionsContain=test"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 200 ok if unexpected query string is set")
        void testSearchRecipeUnknownQueryString() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?unknownQueryString=test"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 500 error if retrieval of recipe failed")
        void testGetRecipesFail() throws Exception {

            doThrow(new RuntimeException()).when(service).search(any());

            mockMvc.perform(get("/recipes"))
                    .andExpect(status().isInternalServerError());
        }
    }


    @Nested
    @DisplayName("PATCH /recipes/{id}")
    class TestUpdateRecipe {
        final Long mockIngredientId1 = 1L;
        final Long mockIngredientId2 = 2L;
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockRecipeId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        List<Ingredient> patchIngredients = List.of(
                new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));
        Recipe patchRecipe = new Recipe( mockRecipeName, mockIsVegetarian,
                mockNumOfServings, mockInstructions, patchIngredients);

        List<Ingredient> mockIngredients = List.of(
                new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));
        Recipe mockRecipe = new Recipe(mockRecipeId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, mockIngredients,  mockCurrentTime);

        @Test
        @DisplayName("return 200 ok and the updated recipes if the update succeeded")
        void testUpdateRecipesSuccess() throws Exception {


            doReturn(Optional.of(mockRecipe)).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(mockRecipeId.intValue())))
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
        @DisplayName("return 404 error if the recipe was not found")
        void testRecipePatchNotFound() throws Exception {


            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the update failed")
        void testRecipeDeleteFailure() throws Exception {
            doReturn(Optional.of(mockRecipe)).when(service).findById(mockRecipeId);
            doThrow(new RuntimeException()).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /recipes/{id}")
    class TestDeleteRecipe {
        final Long mockIngredientId1 = 1L;
        final Long mockIngredientId2 = 2L;
        final String mockIngredientName1 = "test ingredient 1";
        final String mockIngredientName2 = "test ingredient 2";
        final Long mockRecipeId = 1L;
        final String mockRecipeName = "test ingredient 1";
        final boolean mockIsVegetarian = true;
        final Short mockNumOfServings = 2;
        final String mockInstructions = "test 1 instructions";

        List<Ingredient> mockIngredients = List.of(
                new Ingredient(mockIngredientId1, mockIngredientName1),
                new Ingredient(mockIngredientId2, mockIngredientName2));
        Recipe mockRecipe = new Recipe(mockRecipeId, mockRecipeName, mockIsVegetarian,  mockNumOfServings,
                mockInstructions, mockIngredients,  mockCurrentTime);

        @Test
        @DisplayName("return 200 ok if deletion succeeded")
        void testRecipeDeleteSuccess() throws Exception {

            doReturn(Optional.of(mockRecipe)).when(service).findById(mockRecipeId);
            doNothing().when(service).deleteById(mockRecipeId);

            mockMvc.perform(delete("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the recipe was not found")
        void testRecipeDeleteNotFound() throws Exception {
            doReturn(Optional.empty()).when(service).findById(mockRecipeId);

            mockMvc.perform(delete("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("return 500 error if the deletion failed")
        void testRecipeDeleteFailure() throws Exception {

            doReturn(Optional.of(mockRecipe)).when(service).findById(mockRecipeId);
            doThrow(new RuntimeException()).when(service).deleteById(any());

            mockMvc.perform(delete("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isInternalServerError());
        }
    }


}
