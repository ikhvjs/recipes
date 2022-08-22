package github.ikhvjs.recipes.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty @Column(unique = true)
    private String recipe_name;

    @NotNull
    private Boolean is_vegetarian;

    @NotNull
    private short num_servings;

    @Column(length = 2000)
    private String instructions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredients> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public Boolean getIs_vegetarian() {
        return is_vegetarian;
    }

    public void setIs_vegetarian(Boolean is_vegetarian) {
        this.is_vegetarian = is_vegetarian;
    }

    public short getNum_servings() {
        return num_servings;
    }

    public void setNum_servings(short num_servings) {
        this.num_servings = num_servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }
}
