package github.ikhvjs.recipes.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.ikhvjs.recipes.annotation.UniqueRecipeName;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Size(max = 100, message = "size must be between 1 and 100")
    @UniqueRecipeName
    @Column(name="recipe_name", unique = true)
    private String recipeName;

    @NotNull
    @Column(name="is_vegetarian")
    private Boolean isVegetarian;

    @NotNull
    @Range(min = 1, max = 100, message = "range must be between 1 and 100")
    @Column(name="num_servings")
    private Short numOfServings;

    @NotEmpty
    @Size(max = 2000, message = "size must be between 1 and 2000")
    @Column(length = 2000)
    private String instructions;

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeName='" + recipeName + '\'' +
                '}';
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ingredient> ingredients = new ArrayList<>();

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
