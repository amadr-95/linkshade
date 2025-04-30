package com.amador.urlshortener.web.controllers.dto;

import jakarta.validation.constraints.NotBlank;


//TODO: create more realistic url validation (with a custom annotation)
public record ShortUrlForm(
        @NotBlank(message = "{validation.baseUrl.notBlank}")
        String originalUrl
) {
}
