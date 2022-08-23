package github.ikhvjs.recipes.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(name="recipe_name", unique = true)
    private String recipeName;

    @NotNull
    @Column(name="is_vegetarian")
    private Boolean isVegetarian;

    @NotNull
    @Column(name="num_servings")
    private Short numOfServings;

    @Column(length = 2000)
    private String instructions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    @NotNull
    @Column(name="modified_time")
    @UpdateTimestamp
    private LocalDateTime modifiedTime;

    public Recipe(Long id, String recipeName, Boolean isVegetarian, Short numOfServings, String instructions, List<Ingredient> ingredients, LocalDateTime modifiedTime) {
        this.id = id;
        this.recipeName = recipeName;
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.modifiedTime = modifiedTime;
    }

    public Recipe(String recipeName, Boolean isVegetarian, Short numOfServings, String instructions, List<Ingredient> ingredients) {
        this.recipeName = recipeName;
        this.isVegetarian = isVegetarian;
        this.numOfServings = numOfServings;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public Recipe() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
