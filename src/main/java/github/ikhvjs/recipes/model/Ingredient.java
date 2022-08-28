package github.ikhvjs.recipes.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ingredients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "ingredient_name"}))
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotEmpty
    @Size(max = 100, message = "size must be between 1 and 100")
    @Column(name="ingredient_name")
    private String ingredientName;

    @JsonBackReference
    @ManyToOne
    private Recipe recipe;


    public Ingredient() {
    }

    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }
    public Ingredient(Long id, String ingredientName) {
        this.id = id;
        this.ingredientName = ingredientName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
