package de.linkshade.web.controllers.handler;

import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlExpiredException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UrlPrivateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlException.class)
    public String urlExceptionHandler(UrlException ex) {
        log.error("Shorturl problem, reason: {}", ex.getMessage(), ex);
        if (ex instanceof UrlNotFoundException || ex instanceof UrlExpiredException)
            return "error/404"; //not found
        else if (ex instanceof UrlPrivateException) {
            return "error/401"; //unauthorized
        }
        return "error/500"; //server error
    }
}
