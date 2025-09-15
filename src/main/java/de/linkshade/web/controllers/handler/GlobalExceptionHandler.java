package de.linkshade.web.controllers.handler;

import de.linkshade.exceptions.*;
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

    @ExceptionHandler(UserException.class)
    public String userExceptionHandler(UserException ex) {
        log.error("User problem, reason: {}", ex.getMessage(), ex);
        return "error/500";
    }
}
