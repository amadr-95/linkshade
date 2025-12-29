package de.linkshade.web.controllers;

import de.linkshade.web.controllers.helpers.ModelAttributeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/legal")
@RequiredArgsConstructor
public class LegalController {

    private final ModelAttributeHelper helper;

    @GetMapping("/privacy")
    public String privacy(Model model) {
        helper.addAvatarToModel(model);
        return "legal/privacy";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        helper.addAvatarToModel(model);
        return "legal/terms";
    }
}
