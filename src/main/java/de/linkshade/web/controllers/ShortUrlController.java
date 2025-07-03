package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.UrlException;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.ShortUrlService;
import de.linkshade.services.UserService;
import de.linkshade.web.controllers.dto.ShortUrlForm;
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
    private final AuthenticationService authenticationService;

    @GetMapping
    public String home(Model model, @PageableDefault Pageable pageable) {
        loadUrlDataToModel(model, "/", shortUrlService.findAllPublicUrls(pageable));
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
    public String createShortUrl(@ModelAttribute("shortUrlForm") @Valid ShortUrlForm shortUrlForm,
                                 BindingResult bindingResult, //for errors (important! right after the ModelAttribute)
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 @PageableDefault Pageable pageable
    ) {
        //TODO: if the number is too big, it cannot be converted to Integer nor Long, so it fails.
        // When calling index again, the field gets the null instead of the big number introduced, resulting in
        // a server error page.
        if (bindingResult.hasErrors()) {
            loadUrlDataToModel(model, "/", shortUrlService.findAllPublicUrls(pageable));
            return "index";
        }
        try {
            ShortUrlDTO shortUrlDTO = shortUrlService.createShortUrl(shortUrlForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("URL created successfully: %s/s/%s", appProperties.shortUrlProperties().baseUrl(), shortUrlDTO.shortenedUrl()));
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

    @GetMapping("/my-urls")
    public String getUserShortUrls(Model model, @PageableDefault Pageable pageable) {
        loadUrlDataToModel(model, "/my-urls", userService.getUserShortUrls(pageable));
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

    private void loadUrlDataToModel(Model model, String path, PagedResult<ShortUrlDTO> shortUrls) {
        model.addAttribute("userName", authenticationService.getUserName());
        model.addAttribute("pageAvailableSizes", appProperties.pageAvailableSizes());
        model.addAttribute("baseUrl", appProperties.shortUrlProperties().baseUrl());
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("path", path);
    }
}
