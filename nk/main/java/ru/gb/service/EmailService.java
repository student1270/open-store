package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Tizimga kirish uchun tasdiqlash kodi");
            helper.setText("Sizning tasdiqlash kodingiz: <b>" + code + "</b><br>Muddati: 2 daqiqa", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email yuborishda xatolik yuz berdi: " + e.getMessage(), e);
        }
    }
}