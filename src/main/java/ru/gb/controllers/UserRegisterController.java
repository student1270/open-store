package ru.gb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.User;
import ru.gb.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserRegisterController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // AI yozib bergan kod. Lekin tushundim

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") User user, Model model) {
        if (userService.saveUser(user)) {
            UserDetails userDetails = userService.loadUserByUsername(user.getEmailAddress());
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Ro'yxatdan o'tishda xatolik yuz berdi");
            return "register";
        }
    }


/*

// Man yozgan kod. Xuddi AdminController ga o'xshiydi

    @PostMapping("/register")
    public String checkUserRegistration(@RequestParam("surname") String surname,
                                        @RequestParam("name") String name,
                                        @RequestParam("emailAddress") String emailAddress,
                                        @RequestParam("phoneNumber") String phoneNumber,
                                        Model model) {
        User user = new User();
        user.setSurname(surname);
        user.setName(name);
        user.setEmailAddress(emailAddress);
        user.setPhoneNumber(phoneNumber);

        if (userService.saveUser(user)) {
            return "redirect:/login";
        } else {

            model.addAttribute("error", "Ro‘yxatdan o‘tishda xatolik");
            model.addAttribute("user", user);
            return "register";
        }
    }
*/

    @GetMapping("/check-user-details")
    @ResponseBody
    public Map<String, Boolean> checkUserDetails(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        boolean exists = false;

        if (email != null && !email.trim().isEmpty()) {
            exists = userService.isEmailExists(email);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            exists = userService.isPhoneExists(phone);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}
