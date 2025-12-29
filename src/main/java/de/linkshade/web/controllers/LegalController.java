package de.linkshade.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/legal/privacy")
    public String privacy() {
        return "legal/privacy";
    }

    @GetMapping("/legal/terms")
    public String terms() {
        return "legal/terms";
    }
}
