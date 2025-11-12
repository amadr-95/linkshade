package de.linkshade.web.controllers;

import de.linkshade.config.AppProperties;
import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UserException;
import de.linkshade.services.SharingResult;
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
    public String listUserUrls(Model model, @PageableDefault Pageable pageable) {
        helper.addAttributes(model, "/my-urls", shortUrlService.listUserUrls(pageable));
        model.addAttribute("shortUrlEditForm", new ShortUrlEditForm(null,
                null,
                null,
                false,
                false));
        return "user/my-urls";
    }

    @PostMapping("/delete-selected-urls")
    public String deleteSelectedUrls(@RequestParam(name = "urlIds") List<UUID> shortUrlsIds,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("returnUrl") String returnUrl) {
        if (shortUrlsIds == null || shortUrlsIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No URLs selected for deletion");
            return "redirect:" + returnUrl;
        }
        try {
            int deletedUrls = shortUrlService.deleteSelectedUrls(shortUrlsIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("%d URL(s) deleted successfully", deletedUrls));
        } catch (UrlNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting URLs. Try again");
        }
        return "redirect:" + returnUrl;
    }

    //For tradicional html-web-form applications it is common to use POST for editing; PUT for API Rest
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
            log.error("Edit URL problem, reason: '{}'", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error editing the URL. Insert valid values and try again");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/manage-sharing-code/{urlId}")
    public String manageSharingPrivateUrl(@PathVariable("urlId") UUID urlId,
                                          @RequestParam("returnUrl") String returnUrl,
                                          RedirectAttributes redirectAttributes) {
        try {
            SharingResult sharingResult = shortUrlService.manageSharingPrivateUrl(urlId);
            String sharingCode = sharingResult.sharingCode();
            if (sharingCode == null) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Sharing deactivated for '%s/s/%s'",
                                appProperties.shortUrlProperties().baseUrl(), sharingResult.url()));
            } else {
                String shortUrl =
                        String.format("%s/s/%s", appProperties.shortUrlProperties().baseUrl(), sharingResult.url());

                redirectAttributes.addFlashAttribute("shortUrlSuccessful",
                        String.format("Sharing activated for '%s'. CODE: %s",
                                shortUrl, sharingCode));
                redirectAttributes.addFlashAttribute("shortUrlCopyToClipboard",
                        String.format("URL: %s, Code: %s", shortUrl, sharingCode));
            }
        } catch (UrlException ex) {
            log.error("Sharing private URL problem, reason: '{}'", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error sharing this URL. Please try again");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/reactivate-expired-urls")
    public String reactivateUrls(@RequestParam("returnUrl") String returnUrl,
                                 RedirectAttributes redirectAttributes) {
        try {
            int reactivatedExpiredUrls = shortUrlService.reactivateExpiredUrls();
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("%d URL(s) successfully reactivated", reactivatedExpiredUrls));
        } catch (UserException ex) {
            log.error("Reactivation URL problem, reason: '{}'", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error reactivating URLs. Try again");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/delete-expired-urls")
    public String deleteExpiredUrls(@RequestParam("returnUrl") String returnUrl,
                                    RedirectAttributes redirectAttributes) {
        try {
            int deleteExpiredUrls = shortUrlService.deleteExpiredUrls();
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("%d expired URL(s) deleted successfully", deleteExpiredUrls));
        } catch (UserException ex) {
            log.error("Deletion expired URL problem, reason: '{}'", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error deleting expired URLs. Try again");
        }
        return "redirect:" + returnUrl;
    }
}
