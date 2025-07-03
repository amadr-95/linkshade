package de.linkshade.util;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidationContextBuilder {

    public void buildContext(ConstraintValidatorContext context, String msg, String field) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
