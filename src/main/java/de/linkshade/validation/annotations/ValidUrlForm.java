package de.linkshade.validation.annotations;

import de.linkshade.validation.UrlFormValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {UrlFormValidator.class})
public @interface ValidUrlForm {
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "{validation.urlForm.default}";
}
