package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.UrlException;
import de.linkshade.services.ShortUrlService;
import de.linkshade.services.UserService;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final UserService userService;
    private final AppProperties appProperties;
    private final ModelAttributeHelper helper;

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
                                 @PageableDefault Pageable pageable
    ) {
        //TODO: if the number is too big, it cannot be converted to Integer nor Long, so it fails.
        // When calling index again, the field gets the null instead of the big number introduced, resulting in
        // a server error page.
        if (bindingResult.hasErrors()) {
            helper.addAttributes(model, "/", shortUrlService.findAllPublicUrls(pageable));
            return "index";
        }
        try {
            String shortenedUrl = shortUrlService.createShortUrl(shortUrlForm);
            String shortUrlCreated =
                    String.format("%s/s/%s", appProperties.shortUrlProperties().baseUrl(), shortenedUrl);
            redirectAttributes.addFlashAttribute("shortUrlSuccessful",
                    String.format("URL created successfully: %s", shortUrlCreated));
            redirectAttributes.addFlashAttribute("shortUrlCreated", shortUrlCreated);
        } catch (UrlException ex) {
            log.error("Shorturl problem, reason: {}", ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("shortUrlError",
                    "There was an error creating the URL, please try again");
        }
        return "redirect:/";
    }

    @GetMapping("/s/{short-urls}")
    public String accessOriginalUrl(@PathVariable("short-urls") String shortUrl) throws UrlException {
        return "redirect:" + shortUrlService.accessOriginalUrl(shortUrl);
    }

    @GetMapping("/my-urls")
    public String getUserShortUrls(Model model, @PageableDefault Pageable pageable) {
        helper.addAttributes(model, "/my-urls", userService.getUserShortUrls(pageable));
        return "user/my-urls";
    }

    @PostMapping("/delete-urls")
    public String deleteSelectedUrls(@RequestParam(name = "urlIds") List<UUID> shortUrlsIds,
                                     RedirectAttributes redirectAttributes) {
        if (shortUrlsIds == null || shortUrlsIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No URLs selected for deletion");
            return "redirect:/my-urls";
        }
        try {
            userService.deleteSelectedUrls(shortUrlsIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been successfully deleted");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting URLs.Try again");
        }
        return "redirect:/my-urls";
    }
}
