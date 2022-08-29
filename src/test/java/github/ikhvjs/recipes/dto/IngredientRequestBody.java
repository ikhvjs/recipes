package github.ikhvjs.recipes.dto;

public class IngredientRequestBody {

    String ingredientName;

    public IngredientRequestBody(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
