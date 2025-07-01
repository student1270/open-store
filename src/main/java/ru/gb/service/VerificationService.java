package ru.gb.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gb.model.User;
import ru.gb.model.VerificationCode;
import ru.gb.repository.UserRepository;
import ru.gb.repository.VerificationCodeRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService {

    private static final int CODE_LENGTH = 6;
    private static final int REQUEST_LIMIT = 5;
    private static final int REQUEST_WINDOW_MINUTES = 30;
    private static final int BLOCK_DURATION_MINUTES = 20;
    private static final int CODE_EXPIRY_MINUTES = 2;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public LocalDateTime requestVerificationCode(String email, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        Optional<VerificationCode> blockedCode = verificationCodeRepository.findFirstByIpAddressAndBlockedUntilAfter(ipAddress, now);
        if (blockedCode.isPresent()) {
            throw new RuntimeException("Keyinroq urinib ko'ring. Qayta urinish: " + blockedCode.get().getBlockedUntil());
        }

        long requestCount = verificationCodeRepository.countByIpAddressAndCreatedAtAfter(ipAddress, now.minusMinutes(REQUEST_WINDOW_MINUTES));
        if (requestCount >= REQUEST_LIMIT) {
            VerificationCode block = new VerificationCode();
            block.setIpAddress(ipAddress);
            block.setBlockedUntil(now.plusMinutes(BLOCK_DURATION_MINUTES));
            verificationCodeRepository.save(block);
            throw new RuntimeException("30 daqiqada 5 tadan ortiq so'rov yuborildi. Keyinroq urinib ko'ring.");
        }

        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Foydalanuvchi topilmadi.");
        }
        User user = userOptional.get();

        verificationCodeRepository.deleteByUserIdAndIsUsedFalseAndExpiryDateAfter(user.getId(), now);

        String code = generateCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setExpiryDate(now.plusMinutes(CODE_EXPIRY_MINUTES));
        verificationCode.setIpAddress(ipAddress);
        verificationCodeRepository.save(verificationCode);

        emailService.sendVerificationCode(email, code);

        return verificationCode.getExpiryDate();
    }

    @Transactional
    public boolean verifyCode(String email, String code, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();

        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByUserIdAndCodeAndIsUsedFalseAndExpiryDateAfter(user.getId(), code, now);
        if (verificationCode.isPresent()) {
            VerificationCode vc = verificationCode.get();
            vc.setUsed(true);
            verificationCodeRepository.save(vc);
            return true;
        }
        return false;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmailAddress(email);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int num = 100000 + random.nextInt(900000);
        return String.format("%06d", num);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanExpiredCodes() {
        verificationCodeRepository.deleteExpiredOrUsedCodes(LocalDateTime.now());
    }
}