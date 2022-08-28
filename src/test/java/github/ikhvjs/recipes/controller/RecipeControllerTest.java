package github.ikhvjs.recipes.controller;

import github.ikhvjs.recipes.dto.RecipeRequestBody;
import github.ikhvjs.recipes.model.Ingredient;
import github.ikhvjs.recipes.model.Recipe;
import github.ikhvjs.recipes.service.RecipeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static github.ikhvjs.recipes.controller.JsonConverter.toJsonString;
import static org.hamcrest.Matchers.*;
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
public class RecipeControllerTest  {
    @MockBean
    private RecipeService service;

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

            doThrow(new RuntimeException()).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("return 400 Error if the recipe name is null.")
        void testCreateRecipeNameNull() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe( null, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if the recipe name is empty.")
        void testCreateRecipeNameEmpty() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe( "",
                    mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if the size of recipe name is not between 1 and 100.")
        void testCreateRecipeNameSize() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe("*".repeat(101),
                    mockIsVegetarian,
                    mockNumOfServings, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if isVegetarian is null.")
        void testCreateRecipeVegetarianNull() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    null,
                    mockNumOfServings, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("isVegetarian : must not be null")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if the numOfServings is null.")
        void testCreateRecipeServingsNull() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    mockIsVegetarian,
                    null, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("numOfServings : must not be null")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if the range of numOfServings is not between 1 and 100.")
        void testCreateRecipeServingsRange() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    mockIsVegetarian,
                    (short) 120, mockInstructions, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("numOfServings : range must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if instructions is null")
        void testCreateRecipeInstructionsNull() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    mockIsVegetarian,
                    mockNumOfServings, null, postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if instructions is empty")
        void testCreateRecipeInstructionsEmpty() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    mockIsVegetarian,
                    mockNumOfServings, "", postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

        }

        @Test
        @DisplayName("return 400 Error if size of instructions is not between 1 and 2000")
        void testCreateRecipeInstructionsSize() throws Exception {

            List<Ingredient> postIngredients = List.of( new Ingredient(mockIngredientId1, mockIngredientName1),
                    new Ingredient(mockIngredientId2, mockIngredientName2));
            Recipe postRecipe = new Recipe(mockRecipeName,
                    mockIsVegetarian,
                    mockNumOfServings, "*".repeat(2001), postIngredients);

            doReturn(mockRecipe).when(service).create(any());

            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(postRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : size must be between 1 and 2000")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")))
            ;

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
        @DisplayName("return 200 ok if isVegetarian is valid")
        void testSearchRecipeIsVegetarianValid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?isVegetarian="+mockIsVegetarian1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 400 error if isVegetarian is not true nor false")
        void testSearchRecipeIsVegetarianInvalid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?isVegetarian=test"))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("isVegetarian : must be true or false")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));
        }

        @Test
        @DisplayName("return 200 ok if numOfServings is Valid")
        void testSearchRecipeNumOfServingsValid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?numOfServings="+mockNumOfServings1))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 400 error if numOfServings is not between 1 and 100.")
        void testSearchRecipeNumOfServingsInvalid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?numOfServings=101"))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("numOfServings : range must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));
        }

        @Test
        @DisplayName("return 200 ok if includedIngredients is valid")
        void testSearchRecipeIncludeIngredientsValid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?includedIngredients="+mockIngredientName1+","+mockIngredientName2))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 400 error if the size of includedIngredients is not between 1 and 100")
        void testSearchRecipeIncludeIngredientsInvalid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?includeIngredients="+"*".repeat(101)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("includeIngredients : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));
        }

        @Test
        @DisplayName("return 200 ok if excludedIngredients is valid")
        void testSearchRecipeExcludeIngredientsValid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?excludedIngredients="+mockIngredientName1+","+mockIngredientName2))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 400 error if size of excludeIngredients is not between 1 and 100")
        void testSearchRecipeExcludeIngredientsInvalid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?excludeIngredients="+"*".repeat(101)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("excludeIngredients : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));
        }

        @Test
        @DisplayName("return 200 ok if instructionsContains is valid")
        void testSearchRecipeInstructionsContainsValid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?instructionsContains=test"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("return 400 error if size of instructionsContains is not between 1 and 2000")
        void testSearchRecipeInstructionsContainsInvalid() throws Exception {
            doReturn(mockRecipes).when(service).search(any());

            mockMvc.perform(get("/recipes?instructionsContains="+"*".repeat(2001)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructionsContains : size must between 1 and 2000")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes")));
        }

        @Test
        @DisplayName("return 200 ok and ignore unexpected query string if unexpected query string is set")
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
        final Boolean mockIsVegetarian = true;
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
        @DisplayName("return 400 error if the size recipe name is null")
        void testUpdateRecipeNameNull() throws Exception {

            Recipe patchRecipe = new Recipe( null, mockIsVegetarian,
                    mockNumOfServings, mockInstructions, patchIngredients);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }
        @Test
        @DisplayName("return 400 error if the size recipe name is not between 1 and 100.")
        void testUpdateRecipeNameSize() throws Exception {

            Recipe patchRecipe = new Recipe( "*".repeat(101), mockIsVegetarian,
                    mockNumOfServings, mockInstructions, patchIngredients);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("recipeName : size must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if the vegetarian is null.")
        void testUpdateRecipeVegetarianNull() throws Exception {

            Recipe patchRecipe = new Recipe( mockRecipeName, null,
                    mockNumOfServings, mockInstructions, patchIngredients);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("isVegetarian : must not be null")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if the isVegetarian is not true or false.")
        void testUpdateRecipeVegetarianBoolean() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,"test",mockNumOfServings.toString(),mockInstructions);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", containsString("Cannot deserialize value of type `java.lang.Boolean` from String \"test\": only \"true\" or \"false\" recognized")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if the numOfServings is null.")
        void testUpdateRecipeServingsNull() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,Boolean.toString(mockIsVegetarian), null, mockInstructions);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("numOfServings : must not be null")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if the range of numOfServings is not between 1 and 100.")
        void testUpdateRecipeServingsRange() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,Boolean.toString(mockIsVegetarian), "101", mockInstructions);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", containsString("numOfServings : range must be between 1 and 100")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if instructions is null")
        void testUpdateRecipeInstructionNull() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,Boolean.toString(mockIsVegetarian), mockNumOfServings.toString(), null);

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if instructions is empty")
        void testUpdateRecipeInstructionEmpty() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,Boolean.toString(mockIsVegetarian), mockNumOfServings.toString(), "");

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : must not be empty")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 400 error if size of instructions is not between 1 and 2000")
        void testUpdateRecipeInstructionSize() throws Exception {

            RecipeRequestBody body = new RecipeRequestBody(mockRecipeName,Boolean.toString(mockIsVegetarian), mockNumOfServings.toString(), "*".repeat(2001));

            doReturn(Optional.empty()).when(service).findById(mockRecipeId);
            doReturn(mockRecipe).when(service).update(any());

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(body)))

                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.timestamp", is(mockCurrentDateTimeString)))
                    .andExpect(jsonPath("$.messages").isArray())
                    .andExpect(jsonPath("$.messages",hasSize(1)))
                    .andExpect(jsonPath("$.messages[0]", is("instructions : size must be between 1 and 2000")))
                    .andExpect(jsonPath("$.description", is("uri=/recipes/1")));
        }

        @Test
        @DisplayName("return 404 error if the recipe was not found")
        void testUpdateRecipeNotFound() throws Exception {
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

            mockMvc.perform(patch("/recipes/{id}", mockRecipeId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(toJsonString(patchRecipe)))

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
