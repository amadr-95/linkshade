package com.amador.urlshortener.web.controllers;

import com.amador.urlshortener.config.ShortUrlConfig;
import com.amador.urlshortener.services.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final ShortUrlService shortUrlService;
    private final ShortUrlConfig shortUrlConfig;

    @GetMapping
    public String home(@RequestParam(required = false, defaultValue = "Guest") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("baseUrl", shortUrlConfig.baseUrl());
        model.addAttribute("publicUrls", shortUrlService.findAllPublicUrls());
        return "home";
    }
}
