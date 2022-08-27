package github.ikhvjs.recipes.controller;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.util.List;

public class QueryString {

    private Boolean isVegetarian;
    @Range(min = 1, max = 100, message = "numOfServings : range must be between 1 and 100")
    private Short numOfServings;

    private List<@Size(min = 1, max = 100, message = "includeIngredients : size must be between 1 and 100") String>  includeIngredients;

    private List<@Size(min = 1, max = 100, message = "includeIngredients : size must be between 1 and 100") String> excludeIngredients;

    @Size(max = 100, message = "instructionsContains : size must between 0 and 100")
    private String instructionsContains;

    public QueryString(){}

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

    public QueryString(Boolean isVegetarian, Short numOfServings, List<String> includeIngredients, List<String> excludeIngredients, String instructionsContains) {
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.includeIngredients = includeIngredients;
        this.excludeIngredients = excludeIngredients;
        this.instructionsContains = instructionsContains;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Short getNumOfServings() {
        return numOfServings;
    }

    public void setNumOfServings(Short numOfServings) {
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
