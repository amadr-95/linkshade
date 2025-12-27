package de.linkshade.web.controllers.helpers;

import de.linkshade.services.AdminService;
import de.linkshade.web.controllers.dto.ShortUrlEditForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class AdminModelAttributeHelper {

    private final AdminService adminService;

    public void addUrlsManagementAttributes(Model model) {
        model.addAttribute("managedEntity", "URLs");
        model.addAttribute("deleteSelectedFormId", "deleteUrlsForm");
        model.addAttribute("deleteSelectedEndpoint", "/delete-selected-urls");
        model.addAttribute("shortUrlEditForm", createEmptyShortUrlEditForm());
        addExpiredUrlsAttributesIfPresent(model);
    }

    public void addUsersManagementAttributes(Model model) {
        model.addAttribute("managedEntity", "Users");
        model.addAttribute("deleteSelectedFormId", "deleteUsersForm");
        model.addAttribute("deleteSelectedEndpoint", "/admin/dashboard/delete-selected-users");
    }

    private void addExpiredUrlsAttributesIfPresent(Model model) {
        int expiredUrls = adminService.getExpiredUrlsByUserNull().size();
        if (expiredUrls > 0) {
            model.addAttribute("deleteExpiredFormId", "deleteExpiredUrlsFormId");
            model.addAttribute("deleteExpiredUrlsEndpoint", "/admin/dashboard/delete-expired-urls");
            model.addAttribute("expiredUrls", expiredUrls);
        }
    }

    private ShortUrlEditForm createEmptyShortUrlEditForm() {
        return new ShortUrlEditForm(null, null, null, false, false);
    }
}

