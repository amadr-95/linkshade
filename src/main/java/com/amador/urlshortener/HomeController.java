package com.amador.urlshortener;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping()
    public String home(@RequestParam(required = false) String name, Model model) {
        if(name != null && !name.isBlank())
            model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
