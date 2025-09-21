package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.UrlException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final AppProperties appProperties;
    private final ModelAttributeHelper helper;
    private final RateLimitService rateLimitService;

    @GetMapping
    public String home(Model model, @PageableDefault Pageable pageable) {
        helper.addAttributes(model, "/", shortUrlService.findAllPublicUrls(pageable));
        model.addAttribute("shortUrlForm", new ShortUrlForm(
                "",
                null,
                appProperties.shortUrlProperties().isPrivate(),
                appProperties.shortUrlProperties().defaultUrlLength(),
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
        Boolean rateLimitReached = (Boolean) request.getAttribute("rateLimitReached");
        if (rateLimitReached != null && rateLimitReached) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    request.getAttribute("rateLimitMessage"));
            return "redirect:/";
        }

        String rateLimitWarning = (String) request.getAttribute("rateLimitWarning");
        if (rateLimitWarning != null)
            redirectAttributes.addFlashAttribute("warningMessage", rateLimitWarning);

        if (bindingResult.hasErrors()) {
            String clientIpAddress = rateLimitService.getClientIpAddress(request);
            rateLimitService.incrementConsecutiveErrors(clientIpAddress);
            rateLimitService.addExtraToken(clientIpAddress);
            helper.addAttributes(model, "/", shortUrlService.findAllPublicUrls(pageable));
            if (rateLimitWarning != null) model.addAttribute("warningMessage", rateLimitWarning);
            return "index";
        }

        try {
            String shortenedUrl = shortUrlService.createShortUrl(shortUrlForm);
            String shortUrlCreated =
                    String.format("%s/s/%s", appProperties.shortUrlProperties().baseUrl(), shortenedUrl);
            redirectAttributes.addFlashAttribute("shortUrlSuccessful",
                    String.format("URL created successfully: %s", shortUrlCreated));
            redirectAttributes.addFlashAttribute("shortUrlCopyToClipboard", shortUrlCreated);
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
}
