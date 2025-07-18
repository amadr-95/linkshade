package de.linkshade.util.annotations;

import de.linkshade.util.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {PasswordValidator.class})
public @interface ValidPassword {
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "{validation.user.invalidPassword}";
}
