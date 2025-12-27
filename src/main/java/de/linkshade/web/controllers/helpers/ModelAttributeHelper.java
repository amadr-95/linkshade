package de.linkshade.web.controllers.helpers;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class ModelAttributeHelper {

    private final AppProperties appProperties;
    private final AuthenticationService authenticationService;
    private final ShortUrlService shortUrlService;

    public void addAttributes(Model model, String path, PagedResult<?> entities) {
        addCommonDataToModel(model, path);
        model.addAttribute("userName", authenticationService.getUserName());
        model.addAttribute("entities", entities);
        authenticationService.getUserInfo().ifPresent(
                user -> {
                    //use to show the button (that sends the form)
                    model.addAttribute("expiredUrls",
                            shortUrlService.getExpiredUrlsByUserId(user.getId()).size());
                    model.addAttribute("deleteSelectedFormId", "deleteUrlsForm");
                });
        addAvatarToModel(model);
    }

    private void addCommonDataToModel(Model model, String path) {
        model.addAttribute("pageAvailableSizes", appProperties.pageAvailableSizes());
        model.addAttribute("baseUrl", appProperties.shortUrlProperties().baseUrl());
        model.addAttribute("path", path);
        model.addAttribute("enableDeleteAccount", appProperties.enableDeleteAccount());
    }

    public void addAvatarToModel(Model model) {
        authenticationService.getAvatarUrl().ifPresent(
                avatarUrl -> model.addAttribute("avatarUrl", avatarUrl)
        );
    }
}
