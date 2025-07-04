package de.linkshade.web.controllers.dto;

import de.linkshade.util.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(
        @Email(message = "{validation.user.invalidEmail}")
        @NotBlank(message = "{validation.user.blankEmail}")
        String email,
        @ValidPassword
        String password,
        @NotBlank(message = "{validation.user.invalidName}")
        String name
) {
}
