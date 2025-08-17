package de.linkshade.web.controllers.dto;

import de.linkshade.util.annotations.ValidUrlForm;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@ValidUrlForm
public record ShortUrlForm(
        String originalUrl,

        @DateTimeFormat(iso = DATE)
        LocalDate expirationDate,

        Boolean isPrivate,

        Integer urlLength,

        Boolean isCustom,

        String customShortUrlName
) {
}
