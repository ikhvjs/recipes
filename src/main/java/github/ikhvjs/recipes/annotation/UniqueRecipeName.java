package github.ikhvjs.recipes.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueRecipeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueRecipeName {
    String message() default "Recipe Name is already registered";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
