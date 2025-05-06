package com.amador.urlshortener.web.controllers.handler;

import com.amador.urlshortener.exceptions.UrlException;
import com.amador.urlshortener.exceptions.UrlExpiredException;
import com.amador.urlshortener.exceptions.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlException.class)
    public String urlExceptionHandler(UrlException ex) {
        log.error("Problem accessing shorturl, reason: {}", ex.getMessage(), ex);
        if(ex instanceof UrlNotFoundException || ex instanceof UrlExpiredException)
            return "error/404"; //not found
        return "error/500"; //server error
    }

}
