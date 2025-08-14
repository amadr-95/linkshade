package de.linkshade.web.controllers;

import de.linkshade.services.AdminService;
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
    private final ModelAttributeHelper helper;

    @GetMapping("/urls")
    public String getAllShortUrls(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/urls", adminService.findAllShortUrls(pageableRequest));
        model.addAttribute("managedEntity", "URLs");
        model.addAttribute("formId", "deleteUrlsForm");
        model.addAttribute("shortUrlEditForm", new ShortUrlEditForm(null,
                null,
                null,
                false,
                false));
        model.addAttribute("deleteEndpoint", "/delete-urls");
        return "admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String getAllUsers(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/users", adminService.findAllUsers(pageableRequest));
        model.addAttribute("managedEntity", "Users");
        model.addAttribute("formId", "deleteUsersForm");
        model.addAttribute("deleteEndpoint", "/admin/dashboard/delete-users");
        return "admin/admin-dashboard";
    }

    @PostMapping("/delete-users")
    public String deleteSelectedUsers(@RequestParam("userIds") List<Long> userIds,
                                      RedirectAttributes redirectAttributes,
                                      @RequestParam("returnUrl") String returnUrl) {
        if (userIds == null || userIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No users selected for deletion");
            return "redirect:" + returnUrl;
        }
        try {
            int[] usersAndUrlsDeleted = adminService.deleteSelectedUsers(userIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Selected users have been successfully deleted [%s users, %s urls]",
                            usersAndUrlsDeleted[0],
                            usersAndUrlsDeleted[1]));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting Users. Try again");
        }
        return "redirect:" + returnUrl;
    }
}
