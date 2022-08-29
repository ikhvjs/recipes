package github.ikhvjs.recipes.integration;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import github.ikhvjs.recipes.dto.IngredientRequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;


import static github.ikhvjs.recipes.controller.JsonConverter.toJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class IngredientIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource datasource;

    public ConnectionHolder getConnectionHolder() {
        return () -> datasource.getConnection();
    }

    @Nested
    @DisplayName("GET /recipes/{id}/ingredients")
    @DataSet("recipes.yml")
    class TestGetRecipeIngredient {
        @Test
        @DisplayName("return 200 ok if the retrieval succeeded")
        void testRecipeIngredientGetSuccess() throws Exception {

            mockMvc.perform(get("/recipes/{id}/ingredients", 1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[0].ingredientName", is("a")))
                    .andExpect(jsonPath("$[1].ingredientName", is("b")));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientGetNotFound() throws Exception {

            mockMvc.perform(get("/recipes/{id}/ingredients", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /ingredients/{id}")
    @DataSet("recipes.yml")
    class TestGetRecipeById {
        @Test
        @DisplayName("return 200 ok and the recipe if the recipe is found")
        void testGetIngredientByIdSuccess() throws Exception {
            mockMvc.perform(get("/ingredients/{id}", 1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.ingredientName", is("a")));
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testGetIngredientByIdNotFound() throws Exception {
            mockMvc.perform(get("/ingredients/{id}", 999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /recipes/{id}/ingredients")
    @DataSet("recipes.yml")
    class TestCreateRecipeIngredient {
        @Test
        @DisplayName("return 200 ok if the creation succeeded")
        void testRecipeIngredientCreateSuccess() throws Exception {

            final String mockIngredientName = "test 123";

            IngredientRequestBody postIngredient = new IngredientRequestBody(mockIngredientName);

            mockMvc.perform(post("/recipes/{id}/ingredients",1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", is(15)))
                    .andExpect(jsonPath("$.ingredientName", is(mockIngredientName)));
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientCreateNotNull() throws Exception {

            IngredientRequestBody postIngredient = new IngredientRequestBody("test 1234");

            mockMvc.perform(post("/recipes/{id}/ingredients",99)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postIngredient)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /ingredients/{id}")
    @DataSet("recipes.yml")
    class TestUpdateRecipeIngredient {

        @Test
        @DisplayName("return 200 ok if the update succeeded")
        void testRecipeIngredientUpdateSuccess() throws Exception {
            Long mockIngredientId = 1L;
            String mockIngredientName = "test 1234";
            IngredientRequestBody patchIngredient = new IngredientRequestBody(mockIngredientName);

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                    .andExpect(jsonPath("$.id", is(mockIngredientId.intValue())))
                    .andExpect(jsonPath("$.ingredientName", is(mockIngredientName)));
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testRecipeIngredientUpdateRecipeNotFound() throws Exception {
            Long mockIngredientId = 99L;
            String mockIngredientName = "test 1234";
            IngredientRequestBody patchIngredient = new IngredientRequestBody(mockIngredientName);

            mockMvc.perform(patch("/ingredients/{id}", mockIngredientId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchIngredient)))

                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /ingredients/{id}")
    @DataSet("recipes.yml")
    class TestDeleteIngredient {
        @Test
        @DisplayName("return 200 ok if the deletion succeeded")
        void testIngredientDeleteSuccess() throws Exception {
            mockMvc.perform(delete("/ingredients/{id}", 1))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the ingredient is not found")
        void testIngredientDeleteNotFound() throws Exception {
            mockMvc.perform(delete("/ingredients/{id}", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /recipes/{id}/ingredients")
    @DataSet("recipes.yml")
    class TestDeleteRecipeIngredient {
        @Test
        @DisplayName("return 200 ok if the deletion succeeded")
        void testRecipeIngredientsDeleteSuccess() throws Exception {
            mockMvc.perform(delete("/recipes/{id}/ingredients", 1))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 404 error if the recipe is not found")
        void testRecipeIngredientsDeleteRecipeNotFound() throws Exception {
            mockMvc.perform(delete("/recipes/{id}/ingredients", 99))
                    .andExpect(status().isNotFound());
        }

    }

}
