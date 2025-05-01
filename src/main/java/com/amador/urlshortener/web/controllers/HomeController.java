package com.amador.urlshortener.web.controllers;

import com.amador.urlshortener.config.ShortUrlProperties;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.services.ShortUrlService;
import com.amador.urlshortener.web.controllers.dto.ShortUrlForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final ShortUrlService shortUrlService;
    private final ShortUrlProperties shortUrlProperties;

    @GetMapping
    public String home(@RequestParam(required = false, defaultValue = "Guest") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("baseUrl", shortUrlProperties.baseUrl());
        model.addAttribute("publicUrls", shortUrlService.findAllPublicUrls());
        model.addAttribute("shortUrlForm", new ShortUrlForm(""));
        return "home";
    }

    @PostMapping("/short-url")
    public String createShortUrl(@ModelAttribute("shortUrlForm") @Valid ShortUrlForm shortUrlForm,
                                 BindingResult bindingResult, //for errors (important! right after the ModelAttribute)
                                 RedirectAttributes redirectAttributes,
                                 Model model
    ) {
        // When receiving the form, check for errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("baseUrl", shortUrlProperties.baseUrl());
            model.addAttribute("publicUrls", shortUrlService.findAllPublicUrls());
            return "home";
        }
        try {
            ShortUrlDTO shortUrlDTO = shortUrlService.createShortUrl(shortUrlForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "URL created successfully: " + shortUrlProperties.baseUrl() + "/" + shortUrlDTO.shortenedUrl());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "There was an error creating the URL, try again");
        }
        return "redirect:/";
    }
}
