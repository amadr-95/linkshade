package de.linkshade.web.controllers.helpers;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class ModelAttributeHelper {

    private final AppProperties appProperties;
    private final AuthenticationService authenticationService;

    public void addAttributes(Model model, String path, PagedResult<?> entities) {
        addCommonDataToModel(model, path);
        model.addAttribute("userName", authenticationService.getUserName());
        model.addAttribute("entities", entities);
        authenticationService.getUserId().ifPresent(
                id -> model.addAttribute("userId", id));
        authenticationService.getAvatarUrl().ifPresent(
                avatarUrl -> model.addAttribute("avatarUrl", avatarUrl));
    }

    private void addCommonDataToModel(Model model, String path) {
        model.addAttribute("pageAvailableSizes", appProperties.pageAvailableSizes());
        model.addAttribute("baseUrl", appProperties.shortUrlProperties().baseUrl());
        model.addAttribute("path", path);
    }
}
