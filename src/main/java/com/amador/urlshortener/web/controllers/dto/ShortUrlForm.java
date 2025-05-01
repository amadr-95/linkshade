package com.amador.urlshortener.web.controllers.dto;

import com.amador.urlshortener.util.annotations.ValidUrl;

public record ShortUrlForm(
        @ValidUrl
        String originalUrl
) {
}
