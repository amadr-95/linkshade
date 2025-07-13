package de.linkshade.web.controllers;

import de.linkshade.services.AdminService;
import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ModelAttributeHelper helper;

    @GetMapping("/urls")
    public String getAllShortUrls(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/urls", adminService.findAllShortUrls(pageableRequest));
        model.addAttribute("isUrlEntity", true);
        model.addAttribute("managedEntity", "URLs");
        return "admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String getAllUsers(@PageableDefault Pageable pageableRequest, Model model) {
        helper.addAttributes(model, "/admin/dashboard/users", adminService.findAllUsers(pageableRequest));
        model.addAttribute("isUrlEntity", false);
        model.addAttribute("managedEntity", "Users");
        return "admin/admin-dashboard";
    }
}
