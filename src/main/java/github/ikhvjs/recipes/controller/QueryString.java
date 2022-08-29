package github.ikhvjs.recipes.controller;

import javax.validation.constraints.*;
import java.util.List;

public class QueryString {
    @Pattern(regexp = "^true$|^false$", message = "isVegetarian : must be true or false")
    private String isVegetarian;
    @Pattern(regexp = "^[1-9][0-9]?$|^100$", message = "numOfServings : range must be between 1 and 100")
    private String numOfServings;

    private List<@Size(min = 1, max = 100, message = "includeIngredients : size must be between 1 and 100") String>  includeIngredients;

    private List<@Size(min = 1, max = 100, message = "excludeIngredients : size must be between 1 and 100") String> excludeIngredients;

    @Size(min = 1, max = 2000, message = "instructionsContains : size must between 1 and 2000")
    private String instructionsContains;

    public QueryString(){}

    public QueryString(String isVegetarian, String numOfServings, List<String> includeIngredients, List<String> excludeIngredients, String instructionsContains) {
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.includeIngredients = includeIngredients;
        this.excludeIngredients = excludeIngredients;
        this.instructionsContains = instructionsContains;
    }

    @Override
    public String toString() {
        return "QueryString{" +
                "isVegetarian=" + isVegetarian +
                ", numOfServings=" + numOfServings +
                ", includeIngredients=" + includeIngredients +
                ", excludeIngredients=" + excludeIngredients +
                ", instructionsContains='" + instructionsContains + '\'' +
                '}';
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

    public List<String> getIncludeIngredients() {
        return includeIngredients;
    }

    public void setIncludeIngredients(List<String> includeIngredients) {
        this.includeIngredients = includeIngredients;
    }

    public List<String> getExcludeIngredients() {
        return excludeIngredients;
    }

    public void setExcludeIngredients(List<String> excludeIngredients) {
        this.excludeIngredients = excludeIngredients;
    }

    public String getInstructionsContains() {
        return instructionsContains;
    }

    public void setInstructionsContains(String instructionsContains) {
        this.instructionsContains = instructionsContains;
    }
}
