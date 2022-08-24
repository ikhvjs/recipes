package github.ikhvjs.recipes.model;

import java.util.List;

public class QueryString {
    private boolean isVegetarian;
    private Short numOfServings;
    private List<Ingredient> includeIngredients;
    private List<Ingredient> excludeIngredients;
    private String instructionsContain;

    public QueryString(){}

    @Override
    public String toString() {
        return "RequestParams{" +
                "isVegetarian=" + isVegetarian +
                ", numOfServings=" + numOfServings +
                ", includeIngredients=" + includeIngredients +
                ", excludeIngredients=" + excludeIngredients +
                ", instructionsContain='" + instructionsContain + '\'' +
                '}';
    }

    public QueryString(boolean isVegetarian, Short numOfServings, List<Ingredient> includeIngredients, List<Ingredient> excludeIngredients, String instructionsContain) {
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.includeIngredients = includeIngredients;
        this.excludeIngredients = excludeIngredients;
        this.instructionsContain = instructionsContain;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public Short getNumOfServings() {
        return numOfServings;
    }

    public void setNumOfServings(Short numOfServings) {
        this.numOfServings = numOfServings;
    }

    public List<Ingredient> getIncludeIngredients() {
        return includeIngredients;
    }

    public void setIncludeIngredients(List<Ingredient> includeIngredients) {
        this.includeIngredients = includeIngredients;
    }

    public List<Ingredient> getExcludeIngredients() {
        return excludeIngredients;
    }

    public void setExcludeIngredients(List<Ingredient> excludeIngredients) {
        this.excludeIngredients = excludeIngredients;
    }

    public String getInstructionsContain() {
        return instructionsContain;
    }

    public void setInstructionsContain(String instructionsContain) {
        this.instructionsContain = instructionsContain;
    }
}
