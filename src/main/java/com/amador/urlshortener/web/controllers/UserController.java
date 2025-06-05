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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-urls")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AppProperties appProperties;

    @GetMapping
    public String getUserShortUrls(Model model, @PageableDefault Pageable pageable) {
        PagedResult<ShortUrlDTO> userShortUrls = userService.getUserShortUrls(pageable);
        model.addAttribute("pageAvailableSizes", appProperties.pageAvailableSizes());
        model.addAttribute("baseUrl", appProperties.shortUrlProperties().baseUrl());
        model.addAttribute("userShortUrls", userShortUrls);
        model.addAttribute("path", "/my-urls");
        return "user/my-urls";
    }
}
