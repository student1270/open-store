package ru.gb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.User;
import ru.gb.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRegisterRestController {
    private final UserService userService;

    @GetMapping("/api/register")
    public ResponseEntity<?> showRegisterPage() {
        Map<String, Object> response = new HashMap<>();
        response.put("user", new User());
        return ResponseEntity.ok(response);
    }

    // AI yozib bergan kod. Lekin tushundim

    @PostMapping("/api/register")
    public ResponseEntity<?> handleRegister(@RequestBody User user, HttpServletRequest request) {
        if (userService.saveUser(user)) {
            // 1. Foydalanuvchini yuklash
            UserDetails userDetails = userService.loadUserByUsername(user.getEmailAddress());

            // 2. Authentication obyektini yaratish (parolsiz)
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // Parol o'rniga null
                    userDetails.getAuthorities()
            );

            // 3. SecurityContextni yangilash
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 4. Sessionni yangilash (muhim!)
            HttpSession session = request.getSession();
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // 5. Profil sahifasiga yo'naltirish
            Map<String, String> response = new HashMap<>();
            response.put("redirect", "/home");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ro'yxatdan o'tishda xatolik yuz berdi");
            return ResponseEntity.badRequest().body(response);
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

    @GetMapping("/api/check-user-details")
    public ResponseEntity<Map<String, Boolean>> checkUserDetails(
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
        return ResponseEntity.ok(response);
    }
}