package de.linkshade.web.controllers;

import de.linkshade.exceptions.UserEmailDuplicateException;
import de.linkshade.exceptions.UserException;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.UserService;
import de.linkshade.web.controllers.dto.UserRegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

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
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! 🎉 Please log in");
            return "redirect:/login";
        } catch (UserEmailDuplicateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This email is already in use");
            return "redirect:/register";
        }
    }

    @PostMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable("userId") Long userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        try {
            int numberOfUrls = userService.deleteUser(userId);
            //close the user session
            Authentication auth = authenticationService.getAuthentication();
            if (auth != null)
                new SecurityContextLogoutHandler().logout(request, response, auth);
            log.info("User with userId {} and {} urls associated was deleted", userId, numberOfUrls);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("User has been successfully deleted along with %s urls", numberOfUrls));
        } catch (UsernameNotFoundException | UserException ex) {
            log.error("There was an error deleting user with userId {}. {}", userId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error deleting your account. Please try again.");
        }
        return "redirect:/";
    }

}
