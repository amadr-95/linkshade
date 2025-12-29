package de.linkshade.exceptions.handler;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.*;
import de.linkshade.security.AuthenticationService;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ModelAttributeHelper helper;
    private final AuthenticationService authenticationService;
    private final AppProperties appProperties;

    /**
     * Handles URL not found exceptions.
     * Returns 404 error page when a short URL doesn't exist.
     */
    @ExceptionHandler({UrlNotFoundException.class, UrlExpiredException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUrlNotFound(Model model, UrlException ex, HttpServletRequest request) {
        log.warn("URL not found or expired - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildView(model, "error/404");
    }

    /**
     * Handles unauthorized access to private URLs.
     * Returns 401 error page when trying to access a private URL without permission.
     */
    @ExceptionHandler(UrlPrivateException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUrlPrivate(Model model, UrlPrivateException ex, HttpServletRequest request) {
        log.warn("Unauthorized access to private URL - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildView(model, "error/401");
    }

    /**
     * Handles general URL exceptions not covered by specific handlers.
     * Returns 500 error page for unexpected URL-related errors.
     */
    @ExceptionHandler(UrlException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUrlException(Model model, UrlException ex, HttpServletRequest request) {
        log.error("Unexpected URL error - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildView(model, "error/500");
    }

    /**
     * Handles user not found exceptions.
     * Returns 404 error page when a user doesn't exist.
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(Model model, UserNotFoundException ex, HttpServletRequest request) {
        log.warn("User not found - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildView(model, "error/404");
    }

    /**
     * Handles general user exceptions.
     * Returns 500 error page for user-related errors.
     */
    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUserException(Model model, UserException ex, HttpServletRequest request) {
        log.error("User error - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildView(model, "error/500");
    }

    /**
     * Handles validation errors and malformed requests.
     * Returns 400 error page for invalid input or requests.
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            NoResourceFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Model model, Exception ex, HttpServletRequest request) {
        log.warn("Bad request - Path: {}, Type: {}, Message: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildView(model, "error/400");
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public String handleRateLimitExceededException(RateLimitExceededException ex,
                                                   HttpServletRequest request,
                                                   Model model,
                                                   RedirectAttributes redirectAttributes) {
        log.warn("Rate limit exceeded - Path: {}, Type: {}, Message: {}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(), ex.getMessage(), ex);

        String rateLimitMessage = authenticationService.getUserInfo().isPresent() ?
                String.format("You have reached your rate limit. Please wait %d hour before creating more URLs",
                        appProperties.securityProperties().rateLimitDurationHours())
                : String.format("Maximum number of URLs created has been reached. Please either wait %d hour or log in to create more",
                appProperties.securityProperties().rateLimitDurationHours());

        redirectAttributes.addFlashAttribute("errorMessage", rateLimitMessage);
        return buildView(model, "redirect:/");
    }

    /**
     * Handles all uncaught exceptions.
     * Returns 500 error page as a fallback for any unexpected error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnknownException(Model model, Exception ex, HttpServletRequest request) {
        log.error("Unexpected error - Path: {}, Type: {}, Message: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildView(model, "error/500");
    }

    /**
     * Builds an error view by adding user avatar to the model.
     *
     * @param model    the Spring MVC model
     * @param viewName the name of the error view to render
     * @return the view name
     */
    private String buildView(Model model, String viewName) {
        helper.addAvatarToModel(model);
        return viewName;
    }

}
