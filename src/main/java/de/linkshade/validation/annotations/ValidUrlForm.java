package de.linkshade.validation.annotations;

import de.linkshade.validation.ShortUrlFormValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {ShortUrlFormValidator.class})
public @interface ValidUrlForm {
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "{validation.urlForm.default}";
}
