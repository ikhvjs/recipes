package github.ikhvjs.recipes.integration;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import github.ikhvjs.recipes.dto.RecipeRequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static github.ikhvjs.recipes.controller.JsonConverter.toJsonString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecipesIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource datasource;

    public ConnectionHolder getConnectionHolder() {
        return () -> datasource.getConnection();
    }


    @Nested
    @DisplayName("GET /recipes/{id}")
    @DataSet("recipes.yml")
    class TestGetRecipeById {

        @Test
        @DisplayName("return 200 ok and the recipe if the recipe is found")
        void testGetRecipeByIdSuccess() throws Exception {

            mockMvc.perform(get("/recipes/{id}", 1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.recipeName", is("test1")))
                    .andExpect(jsonPath("$.isVegetarian", is(false)))
                    .andExpect(jsonPath("$.numOfServings", is(2)))
                    .andExpect(jsonPath("$.instructions", is("aaa")))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientName", is("a")))
                    .andExpect(jsonPath("$.ingredients[1].ingredientName", is("b")));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testGetRecipeByIdNotFound() throws Exception {
            mockMvc.perform(get("/recipes/{id}", 30))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    @DisplayName("POST /recipes")
    @DataSet("recipes.yml")
    class TestCreateRecipe {

        @Test
        @DisplayName("return 200 ok and the recipe if the creation succeeded")
        void testCreateRecipeSuccess() throws Exception {
            final String mockRecipeName = "test ingredient 1";
            final boolean mockIsVegetarian = true;
            final Short mockNumOfServings = 2;
            final String mockInstructions = "test 1 instructions";

            RecipeRequestBody postRecipe = new RecipeRequestBody(
                    mockRecipeName,
                    Boolean.toString(mockIsVegetarian),
                    mockNumOfServings.toString(),
                    mockInstructions);

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(header().string(HttpHeaders.LOCATION, "/recipes/" + 8))

                    .andExpect(jsonPath("$.id", is(8)))
                    .andExpect(jsonPath("$.recipeName", is(mockRecipeName)))
                    .andExpect(jsonPath("$.isVegetarian", is(mockIsVegetarian)))
                    .andExpect(jsonPath("$.numOfServings", is(mockNumOfServings.intValue())))
                    .andExpect(jsonPath("$.instructions", is(mockInstructions)))
                    .andExpect(jsonPath("$.ingredients", hasSize(0)));
        }

        @Test
        @DisplayName("return 400 Error if the recipe name is registered.")
        void testCreateRecipeNameUnique() throws Exception {

            final String mockRecipeName = "test1";
            final boolean mockIsVegetarian = true;
            final Short mockNumOfServings = 2;
            final String mockInstructions = "test 1 instructions";

            RecipeRequestBody postRecipe = new RecipeRequestBody(
                    mockRecipeName,
                    Boolean.toString(mockIsVegetarian),
                    mockNumOfServings.toString(),
                    mockInstructions);

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : Recipe Name is already registered")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));

        }

    }

    @Nested
    @DisplayName("GET /recipes")
    @DataSet("recipes.yml")
    class TestGetRecipes {
        @Test
        @DisplayName("return 200 ok and 7 recipes if there is no filtering")
        void testGetRecipesNoFilter() throws Exception {

            mockMvc.perform(get("/recipes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(7)));
        }

        @Test
        @DisplayName("return 200 ok and 4 recipes if isVegetarian is true")
        void testGetRecipesVegetarian() throws Exception {

            mockMvc.perform(get("/recipes?isVegetarian=true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(4)));
        }

        @Test
        @DisplayName("return 200 ok and 2 recipes if numOfServings is 2")
        void testGetRecipesServings() throws Exception {

            mockMvc.perform(get("/recipes?numOfServings=2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        @DisplayName("return 200 ok and 2 recipes if instructionsContains is c")
        void testGetRecipesInstructions() throws Exception {

            mockMvc.perform(get("/recipes?instructionsContains=c"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        @DisplayName("return 200 ok and 2 recipes if includeIngredients contains [a,b]")
        void testGetRecipesIncludeIngredients() throws Exception {

            mockMvc.perform(get("/recipes?includeIngredients=a,b"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(5)));
        }

        @Test
        @DisplayName("return 200 ok and 2 recipes if excludeIngredients contains [a,b]")
        void testGetRecipesExcludeIngredients() throws Exception {

            mockMvc.perform(get("/recipes?excludeIngredients=a,b"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("return 200 ok and 2 recipes if multiple filter is set")
        void testGetRecipesMultipleFilter() throws Exception {

            mockMvc.perform(get("/recipes?includeIngredients=a,b&instructionsContains=c&isVegetarian=true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("PATCH /recipes/{id}")
    @DataSet("recipes.yml")
    class TestUpdateRecipe {
        @Test
        @DisplayName("return 200 ok and the updated recipes if the update succeeded")
        void testUpdateRecipesSuccess() throws Exception {
            final Long mockRecipeId = 1L;
            final String mockRecipeName = "test ingredient 1 update";
            final boolean mockIsVegetarian = true;
            final Short mockNumOfServings = 5;
            final String mockInstructions = "test 1 instructions";

            RecipeRequestBody patchRecipe = new RecipeRequestBody(
                    mockRecipeName,
                    Boolean.toString(mockIsVegetarian),
                    mockNumOfServings.toString(),
                    mockInstructions);

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
                    .andExpect(jsonPath("$.ingredients", hasSize(2)));
        }

        @Test
        @DisplayName("return 404 error if the recipe was not found")
        void testUpdateRecipeNotFound() throws Exception {

            final Long mockRecipeId = 99L;
            final String mockRecipeName = "test ingredient 1 update";
            final boolean mockIsVegetarian = true;
            final Short mockNumOfServings = 5;
            final String mockInstructions = "test 1 instructions";

            RecipeRequestBody patchRecipe = new RecipeRequestBody(
                    mockRecipeName,
                    Boolean.toString(mockIsVegetarian),
                    mockNumOfServings.toString(),
                    mockInstructions);

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /recipes/{id}")
    @DataSet("recipes.yml")
    class TestDeleteRecipe {
        @Test
        @DisplayName("return 200 ok if deletion succeeded")
        void testRecipeDeleteSuccess() throws Exception {
            final Long mockRecipeId = 1L;
            mockMvc.perform(delete("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the recipe was not found")
        void testRecipeDeleteNotFound() throws Exception {
            final Long mockRecipeId = 199L;
            mockMvc.perform(delete("/recipes/{id}", mockRecipeId))
                    .andExpect(status().isNotFound());
        }
    }

}