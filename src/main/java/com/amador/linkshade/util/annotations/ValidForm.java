package com.amador.linkshade.util.annotations;

import com.amador.linkshade.util.FormValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {FormValidator.class})
public @interface ValidForm {
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "{validation.urlForm.default}";
}
