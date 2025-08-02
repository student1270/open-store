

package ru.gb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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

//    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);
//
//    @Autowired
//    private VerificationService verificationService;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/login")
//    public String login() {
//        return "user-login";
//    }
//
//    @PostMapping("/api/check-email")
//    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> payload) {
//        String email = payload.get("email");
//        if (email == null || email.isBlank()) {
//            log.warn("Noto‘g‘ri email kiritildi: {}", email);
//            return ResponseEntity.badRequest().body(new CheckEmailResponse(false, "Email manzili yuborilmadi."));
//        }
//
//        Optional<User> user = verificationService.findUserByEmail(email);
//        if (user.isPresent()) {
//            log.info("Email topildi: {}", email);
//            return ResponseEntity.ok().body(new CheckEmailResponse(true, "Email topildi."));
//        }
//        log.warn("Email topilmadi: {}", email);
//        return ResponseEntity.badRequest().body(new CheckEmailResponse(false, "Email topilmadi."));
//    }
//
//    @PostMapping("/api/send-sms")
//    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> payload, HttpServletRequest request) {
//        String email = payload.get("email");
//        if (email == null || email.isBlank()) {
//            log.warn("Noto‘g‘ri email kiritildi: {}", email);
//            return ResponseEntity.badRequest().body(new SendSmsResponse(false, "Email manzili yuborilmadi.", null));
//        }
//
//        try {
//            LocalDateTime expiryDate = verificationService.requestVerificationCode(email, request);
//            String expiryDateStr = expiryDate.toString();
//            log.info("Tasdiqlash kodi yuborildi: email={}, expiry={}", email, expiryDateStr);
//            return ResponseEntity.ok().body(new SendSmsResponse(true, "Tasdiqlash kodi yuborildi.", expiryDateStr));
//        } catch (RuntimeException e) {
//            log.error("SMS yuborishda xato: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(new SendSmsResponse(false, e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/api/verify-sms")
//    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> payload, HttpServletRequest request) {
//        String email = payload.get("email");
//        String code = payload.get("code");
//
//        if (email == null || email.isBlank() || code == null || code.isBlank()) {
//            log.warn("Noto‘g‘ri ma’lumot: email={}, code={}", email, code);
//            return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Email yoki kod yuborilmadi."));
//        }
//
//        boolean verified = verificationService.verifyCode(email, code, request);
//        if (verified) {
//            Optional<User> userOptional = verificationService.findUserByEmail(email);
//            if (userOptional.isPresent()) {
//                UserDetails userDetails = userService.loadUserByUsername(email);
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                // Sessiyaga autentifikatsiyani saqlash
//                request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
//                log.info("Foydalanuvchi autentifikatsiya qilindi: email={}, roli={}", email, userDetails.getAuthorities());
//                return ResponseEntity.ok().body(new VerifySmsResponse(true, "Kod tasdiqlandi, tizimga kirish muvaffaqiyatli."));
//            }
//            log.warn("Foydalanuvchi topilmadi: email={}", email);
//            return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Foydalanuvchi topilmadi."));
//        }
//        log.warn("Noto‘g‘ri yoki muddati o‘tgan kod: email={}, code={}", email, code);
//        return ResponseEntity.badRequest().body(new VerifySmsResponse(false, "Noto‘g‘ri yoki muddati o‘tgan kod."));
//    }
//
//    // Javob modellari
//    static class CheckEmailResponse {
//        boolean exists;
//        String message;
//
//        public CheckEmailResponse(boolean exists, String message) {
//            this.exists = exists;
//            this.message = message;
//        }
//
//        public boolean isExists() { return exists; }
//        public String getMessage() { return message; }
//    }
//
//    static class SendSmsResponse {
//        boolean success;
//        String message;
//        String expiryDate;
//
//        public SendSmsResponse(boolean success, String message, String expiryDate) {
//            this.success = success;
//            this.message = message;
//            this.expiryDate = expiryDate;
//        }
//
//        public boolean isSuccess() { return success; }
//        public String getMessage() { return message; }
//        public String getExpiryDate() { return expiryDate; }
//    }
//
//    static class VerifySmsResponse {
//        boolean success;
//        String message;
//
//        public VerifySmsResponse(boolean success, String message) {
//            this.success = success;
//            this.message = message;
//        }
//
//        public boolean isSuccess() { return success; }
//        public String getMessage() { return message; }
//    }
}