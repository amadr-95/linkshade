package com.amador.urlshortener.web.controllers;

import com.amador.urlshortener.config.AppProperties;
import com.amador.urlshortener.domain.entities.PagedResult;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AppProperties appProperties;

    @GetMapping("/my-urls")
    public String getUserShortUrls(Model model, @PageableDefault Pageable pageable) {
        PagedResult<ShortUrlDTO> userShortUrls = userService.getUserShortUrls(pageable);
        model.addAttribute("pageAvailableSizes", appProperties.pageAvailableSizes());
        model.addAttribute("baseUrl", appProperties.shortUrlProperties().baseUrl());
        model.addAttribute("userShortUrls", userShortUrls);
        model.addAttribute("path", "/my-urls");
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
}
