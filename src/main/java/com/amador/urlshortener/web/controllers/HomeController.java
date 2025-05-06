package com.amador.urlshortener.web.controllers;

import com.amador.urlshortener.config.ShortUrlProperties;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.exceptions.UrlException;
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
    public String home(Model model) {
        getHomePage(model);
        model.addAttribute("shortUrlForm", new ShortUrlForm(""));
        return "index";
    }

    @PostMapping("/short-url")
    public String createShortUrl(@ModelAttribute("shortUrlForm") @Valid ShortUrlForm shortUrlForm,
                                 BindingResult bindingResult, //for errors (important! right after the ModelAttribute)
                                 RedirectAttributes redirectAttributes,
                                 Model model
    ) {
        // When receiving the form, check for errors
        if (bindingResult.hasErrors()) { //if errors, display the home page again
            getHomePage(model);
            return "index";
        }
        try {
            ShortUrlDTO shortUrlDTO = shortUrlService.createShortUrl(shortUrlForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "URL created successfully: " + shortUrlProperties.baseUrl() + "/s/" + shortUrlDTO.shortenedUrl());
        } catch (UrlException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "There was an error creating the URL, try again");
        }
        return "redirect:/";
    }

    @GetMapping("/s/{short-url}")
    public String accessOriginalUrl(@PathVariable("short-url") String shortUrl) throws UrlException {
        return "redirect:" + shortUrlService.accessOriginalUrl(shortUrl);
    }

    private void getHomePage(Model model) {
        model.addAttribute("baseUrl", shortUrlProperties.baseUrl());
        model.addAttribute("publicUrls", shortUrlService.findAllPublicUrls());
    }
}
