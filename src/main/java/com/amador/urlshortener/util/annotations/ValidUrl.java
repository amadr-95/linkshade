package com.amador.urlshortener.util.annotations;

import com.amador.urlshortener.util.UrlValidatorAdvance;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {UrlValidatorAdvance.class})
public @interface ValidUrl {
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default "{validation.urlForm.invalidUrl}";
}
