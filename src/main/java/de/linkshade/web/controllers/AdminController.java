package de.linkshade.web.controllers;

import de.linkshade.exceptions.UserException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.services.AdminService;
import de.linkshade.services.DeletionResult;
import de.linkshade.services.ShortUrlService;
import de.linkshade.web.controllers.dto.ShortUrlEditForm;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ShortUrlService shortUrlService;
    private final ShortUrlRepository shortUrlRepository;
    private final ModelAttributeHelper helper;

    @GetMapping("/urls")
    public String getAllShortUrls(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/urls", shortUrlService.findAllShortUrls(pageableRequest));
        model.addAttribute("managedEntity", "URLs");
        model.addAttribute("deleteSelectedFormId", "deleteUrlsForm");
        model.addAttribute("deleteSelectedEndpoint", "/delete-selected-urls");
        int numberOfExpiredUrls = shortUrlRepository.numberOfExpiredUrls();
        if (numberOfExpiredUrls > 0) {
            model.addAttribute("deleteExpiredFormId", "deleteExpiredUrlsFormId");
            model.addAttribute("deleteExpiredUrlsEndpoint", "/admin/dashboard/delete-expired-urls");
            model.addAttribute("existExpiredUrls", true);
            model.addAttribute("numberOfUrlExpired", numberOfExpiredUrls);
        }
        model.addAttribute("shortUrlEditForm", new ShortUrlEditForm(null,
                null,
                null,
                false,
                false));
        return "admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String getAllUsers(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/users", adminService.findAllUsers(pageableRequest));
        model.addAttribute("managedEntity", "Users");
        model.addAttribute("deleteSelectedFormId", "deleteUsersForm");
        model.addAttribute("deleteSelectedEndpoint", "/admin/dashboard/delete-selected-users");

        return "admin/admin-dashboard";
    }

    @PostMapping("/delete-expired-urls")
    public String deleteAllExpiredUrls(RedirectAttributes redirectAttributes,
                                       @RequestParam("returnUrl") String returnUrl) {
        int numberOfDeletedUrls = adminService.deleteAllExpiredUrls();

        redirectAttributes.addFlashAttribute("successMessage",
                "Expired URLs have been successfully deleted");

        return "redirect:" + returnUrl;
    }

    @PostMapping("/delete-selected-users")
    public String deleteSelectedUsers(@RequestParam("userIds") List<Long> userIds,
                                      RedirectAttributes redirectAttributes,
                                      @RequestParam("returnUrl") String returnUrl) {
        if (userIds == null || userIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No users selected for deletion");
            return "redirect:" + returnUrl;
        }
        try {
            DeletionResult deletionResult = adminService.deleteSelectedUsers(userIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Selected users have been successfully deleted [%s users, %s urls]",
                            deletionResult.usersDeleted(),
                            deletionResult.urlsDeleted()));
        } catch (UserException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting Users. Try again");
        }
        return "redirect:" + returnUrl;
    }
}
