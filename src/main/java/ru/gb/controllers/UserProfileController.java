

package ru.gb.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.service.impl.UserDetailsImpl;

@Controller
@RequestMapping("/user")
public class UserProfileController {
    @GetMapping
    public String showUserProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("userName", userDetails.getUser().getName() + " " + userDetails.getUser().getSurname());
            model.addAttribute("userId", userDetails.getUser().getId());
        }
        return "profile-user";
    }
}