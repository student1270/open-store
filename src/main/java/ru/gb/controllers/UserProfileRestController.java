package ru.gb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.service.impl.UserDetailsImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserProfileRestController {

    @GetMapping
    public ResponseEntity<?> showUserProfile(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            response.put("userName", userDetails.getUser().getName() + " " + userDetails.getUser().getSurname());
            response.put("userId", userDetails.getUser().getId());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Foydalanuvchi autentifikatsiya qilinmagan");
            return ResponseEntity.status(401).body(response);
        }
    }
}
