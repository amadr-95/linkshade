package de.linkshade.web.controllers;

import de.linkshade.exceptions.UserException;
import de.linkshade.services.AdminService;
import de.linkshade.services.DeletionResult;
import de.linkshade.services.ShortUrlService;
import de.linkshade.web.controllers.helpers.AdminModelAttributeHelper;
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
    private final AdminModelAttributeHelper adminHelper;
    private final ModelAttributeHelper helper;

    @GetMapping("/urls")
    public String getAllShortUrls(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/urls", shortUrlService.findAllShortUrls(pageableRequest));
        adminHelper.addUrlsManagementAttributes(model);
        return "admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String getAllUsers(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/users", adminService.findAllUsers(pageableRequest));
        adminHelper.addUsersManagementAttributes(model);
        return "admin/admin-dashboard";
    }

    @PostMapping("/delete-expired-urls")
    public String deleteAllExpiredUrls(RedirectAttributes redirectAttributes,
                                       @RequestParam("returnUrl") String returnUrl) {
        int numberOfDeletedUrls = adminService.deleteAllExpiredUrls();

        redirectAttributes.addFlashAttribute("successMessage",
                String.format("%d expired URL(s) successfully deleted", numberOfDeletedUrls));

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
                    String.format("Selected users have been successfully deleted (%d user(s), %d url(s))",
                            deletionResult.usersDeleted(),
                            deletionResult.urlsDeleted()));
        } catch (UserException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting users. Try again");
        }
        return "redirect:" + returnUrl;
    }
}
