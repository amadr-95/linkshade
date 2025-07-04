package de.linkshade.util;

import de.linkshade.util.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank())
            return false;

        return ValidationConstants.PASSWORD_PATTERN.matcher(password).matches();
    }
}
