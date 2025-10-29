package de.linkshade.web.controllers.handler;

import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlExpiredException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UrlPrivateException;
import de.linkshade.exceptions.UserException;
import de.linkshade.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuthenticationService authenticationService;

    @ExceptionHandler(UrlException.class)
    public String urlExceptionHandler(Model model, UrlException ex) {
        addAvatarToModel(model);
        if (ex instanceof UrlNotFoundException || ex instanceof UrlExpiredException) {
            log.warn("Shorturl not found or expired: {}", ex.getMessage(), ex);
            return "error/404";
        }
        else if (ex instanceof UrlPrivateException) {
            log.warn("Unauthorized access to private URL: {}", ex.getMessage(), ex);
            return "error/401";
        }
        log.error("Unexpected URL error: {}", ex.getMessage(), ex);
        return "error/500";
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public String handleBadRequest(Model model, Exception ex) {
        addAvatarToModel(model);
        log.warn("Bad request error, reason: {}", ex.getMessage(), ex);
        return "error/400";
    }

    @ExceptionHandler(UserException.class)
    public String userExceptionHandler(Model model, UserException ex) {
        addAvatarToModel(model);
        log.error("User problem, reason: {}", ex.getMessage(), ex);
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String unknownException(Model model, Exception ex) {
        addAvatarToModel(model);
        log.error("Unknown error occurred: {}", ex.getMessage(), ex);
        return "error/400";
    }

    private void addAvatarToModel(Model model) {
        authenticationService.getAvatarUrl().ifPresent(
                avatarUrl -> model.addAttribute("avatarUrl", avatarUrl)
        );
    }
}
