package ru.gb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.User;
import ru.gb.service.UserService;
import ru.gb.service.VerificationService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "user-login";
    }

    @PostMapping("/api/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new CheckEmailResponse(false, "Email manzili yuborilmadi."));
        }

        Optional<User> user = verificationService.findUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(new CheckEmailResponse(true, "Email topildi."));
        }
        return ResponseEntity.badRequest().body(new CheckEmailResponse(false, "Email topilmadi."));
    }

    @PostMapping("/api/send-sms")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new SendSmsResponse(false, "Email manzili yuborilmadi.", null));
        }

        try {
            LocalDateTime expiryDate = verificationService.requestVerificationCode(email, request);
            String expiryDateStr = expiryDate.toString(); // format: "2025-07-01T15:03:24"
            return ResponseEntity.ok().body(new SendSmsResponse(true, "Tasdiqlash kodi yuborildi.", expiryDateStr));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new SendSmsResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/api/verify-sms")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        String code = payload.get("code");

        if (email == null || email.isBlank() || code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Email yoki kod yuborilmadi."));
        }

        boolean verified = verificationService.verifyCode(email, code, request);
        if (verified) {
            Optional<User> userOptional = verificationService.findUserByEmail(email);
            if (userOptional.isPresent()) {
                UserDetails userDetails = userService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                return ResponseEntity.ok().body(new VerifySmsResponse(true, "Kod tasdiqlandi, tizimga kirish muvaffaqiyatli."));
            }
            return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Foydalanuvchi topilmadi."));
        }
        return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Noto'g'ri yoki muddati o'tgan kod."));
    }

    // Javob modellari
    static class CheckEmailResponse {
        boolean exists;
        String message;

        public CheckEmailResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }

        public boolean isExists() { return exists; }
        public String getMessage() { return message; }
    }

    static class SendSmsResponse {
        boolean success;
        String message;
        String expiryDate; // LocalDateTime o‘rniga String

        public SendSmsResponse(boolean success, String message, String expiryDate) {
            this.success = success;
            this.message = message;
            this.expiryDate = expiryDate;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getExpiryDate() { return expiryDate; }
    }

    static class VerifySmsResponse {
        boolean success;
        String message;

        public VerifySmsResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
