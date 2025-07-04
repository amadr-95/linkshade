package de.linkshade.web.controllers;

import de.linkshade.exceptions.UserEmailDuplicateException;
import de.linkshade.services.UserService;
import de.linkshade.web.controllers.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest(
                null,
                null,
                null
        ));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserRegistrationRequest userRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(userRequest);
            redirectAttributes.addFlashAttribute("registrationSuccessful",
                    "Registration successful! 🎉 Please log in");
            return "redirect:/login";
        } catch (UserEmailDuplicateException ex) {
            redirectAttributes.addFlashAttribute("registrationError",
                    "This email is already in use");
            return "redirect:/register";
        }
    }

}
