package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UserException;
import de.linkshade.services.ShortUrlService;
import de.linkshade.web.controllers.dto.ShortUrlEditForm;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserUrlController {

    private final ShortUrlService shortUrlService;
    private final AppProperties appProperties;
    private final ModelAttributeHelper helper;

    @GetMapping("/my-urls")
    public String getUserShortUrls(Model model, @PageableDefault Pageable pageable) throws UserException {
        helper.addAttributes(model, "/my-urls", shortUrlService.getUserShortUrls(pageable));
        model.addAttribute("shortUrlEditForm", new ShortUrlEditForm(null,
                null,
                null,
                false,
                false));
        return "user/my-urls";
    }

    @PostMapping("/delete-urls")
    public String deleteSelectedUrls(@RequestParam(name = "urlIds") List<UUID> shortUrlsIds,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("returnUrl") String returnUrl) {
        if (shortUrlsIds == null || shortUrlsIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No URLs selected for deletion");
            return "redirect:" + returnUrl;
        }
        try {
            shortUrlService.deleteSelectedUrls(shortUrlsIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been successfully deleted");
        } catch (UrlNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting URLs. Try again");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/edit-urls/{urlId}")
    public String editUrl(@PathVariable("urlId") UUID urlId,
                          @RequestParam("returnUrl") String returnUrl,
                          @ModelAttribute ShortUrlEditForm shortUrlEditForm,
                          RedirectAttributes redirectAttributes) {
        try {
            String shortenedUrl = shortUrlService.updateUrl(urlId, shortUrlEditForm);
            String shortUrlUpdated =
                    String.format("%s/s/%s", appProperties.shortUrlProperties().baseUrl(), shortenedUrl);
            redirectAttributes.addFlashAttribute("shortUrlSuccessful",
                    String.format("URL updated successfully: %s", shortUrlUpdated));
            redirectAttributes.addFlashAttribute("shortUrlCopyToClipboard", shortUrlUpdated);
        } catch (UrlException ex) {
            //TODO: provide better error messages to the user (message exception from the service)
            log.error("Edit URL problem, reason: '{}'", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error editing the URL. Insert valid values and try again");
        }
        return "redirect:" + returnUrl;
    }
}
