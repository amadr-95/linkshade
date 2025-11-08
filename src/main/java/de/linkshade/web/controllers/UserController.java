package de.linkshade.web.controllers;

import de.linkshade.exceptions.UserNotFoundException;
import de.linkshade.security.AuthenticationService;
import de.linkshade.security.oauth.OAuth2UserImpl;
import de.linkshade.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

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

    @PostMapping("/delete-user")
    public String deleteUserById(HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = authenticationService.getAuthentication();
        UUID userId = ((OAuth2UserImpl) auth.getPrincipal()).user().getId();
        try {
            int numberOfUrls = userService.deleteUser(userId);
            //close the user session
            new SecurityContextLogoutHandler().logout(request, response, auth);
            log.info("User with userId '{}' and {} urls associated was deleted", userId, numberOfUrls);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("User has been successfully deleted along with %d url(s)", numberOfUrls));
        } catch (UserNotFoundException ex) {
            log.error("There was an error deleting user with userId '{}'. {}", userId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error deleting your account. Please try again.");
        }
        return "redirect:/";
    }

}
