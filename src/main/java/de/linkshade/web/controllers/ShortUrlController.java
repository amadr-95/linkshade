package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.UrlException;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.RateLimitService;
import de.linkshade.services.ShortUrlService;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

import static de.linkshade.util.Constants.REMAINING_TOKENS_WARNING;


@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final AppProperties appProperties;
    private final ModelAttributeHelper helper;
    private final RateLimitService rateLimitService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public String home(Model model, @PageableDefault Pageable pageable) {
        helper.addAttributes(model, "/", shortUrlService.findAllPublicUrls(pageable));
        model.addAttribute("shortUrlForm", new ShortUrlForm(
                "",
                null,
                appProperties.shortUrlProperties().isPrivate(),
                appProperties.shortUrlProperties().defaultShortUrlLength(),
                appProperties.shortUrlProperties().isCustom(),
                ""));
        return "index";
    }

    @PostMapping("/short-urls")
    public String createShortUrl(@ModelAttribute @Valid ShortUrlForm shortUrlForm,
                                 BindingResult bindingResult, //for errors (important! right after the ModelAttribute)
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 @PageableDefault Pageable pageable,
                                 HttpServletRequest request
    ) {

        if (rateLimitReached(request, redirectAttributes)) return "redirect:/";

        if (bindingResult.hasErrors()) {
            if (bindingResult.getFieldError("expirationDate") != null) {
                log.warn("User {} is introducing wrong values intentionally. Expected: date, got: {}",
                        authenticationService.getUserInfo().orElse(null),
                        Objects.requireNonNull(bindingResult.getFieldError("expirationDate")).getRejectedValue());
            }
            helper.addAttributes(model, "/", shortUrlService.findAllPublicUrls(pageable));
            return "index";
        }

        try {
            String shortenedUrl = shortUrlService.createShortUrl(shortUrlForm);
            checkAvailableTokens(redirectAttributes, request);
            addSuccessMessage(shortenedUrl, redirectAttributes);
        } catch (UrlException ex) {
            log.error("Shorturl problem, reason: {}", ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error creating the URL, please try again");
        }
        return "redirect:/";
    }

    @GetMapping("/s/{short-urls}")
    public String accessOriginalUrl(@PathVariable("short-urls") String shortUrl) throws UrlException {
        return "redirect:" + shortUrlService.accessOriginalUrl(shortUrl);
    }

    private boolean rateLimitReached(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Boolean rateLimitReached = (Boolean) request.getAttribute("rateLimitReached");
        if (rateLimitReached != null && rateLimitReached) {
            String rateLimitMessage = authenticationService.getUserInfo().isPresent() ?
                    "You have reached your rate limit. Please wait 1 hour before creating more URLs"
                    : "Maximum number of URLs created has been reached. Please either wait 1 hour or log in to create more";
            redirectAttributes.addFlashAttribute("errorMessage", rateLimitMessage);
            return true;
        }
        return false;
    }

    private void checkAvailableTokens(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String clientIpAddress = rateLimitService.getClientIpAddress(request);
        long remainingTokens = rateLimitService.consumeToken(clientIpAddress);
        if (remainingTokens <= REMAINING_TOKENS_WARNING) {
            redirectAttributes.addFlashAttribute("warningMessage",
                    remainingTokens == 0 ? "You ran out of URLs to be created!" :
                            String.format("You have %s URL(s) left to be created", remainingTokens));
        }
    }

    private void addSuccessMessage(String shortenedUrl, RedirectAttributes redirectAttributes) {
        String shortUrlCreated =
                String.format("%s/s/%s", appProperties.shortUrlProperties().baseUrl(), shortenedUrl);
        redirectAttributes.addFlashAttribute("shortUrlSuccessful",
                String.format("URL created successfully: %s", shortUrlCreated));
        redirectAttributes.addFlashAttribute("shortUrlCopyToClipboard", shortUrlCreated);
    }
}
