package github.ikhvjs.recipes.dto;

public class RecipeRequestBody {
    String recipeName;
    String isVegetarian;
    String numOfServings;
    String instructions;

    public RecipeRequestBody(String recipeName, String isVegetarian, String numOfServings, String instructions) {
        this.recipeName = recipeName;
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.instructions = instructions;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(String isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getNumOfServings() {
        return numOfServings;
    }

    public void setNumOfServings(String numOfServings) {
        this.numOfServings = numOfServings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }


}
