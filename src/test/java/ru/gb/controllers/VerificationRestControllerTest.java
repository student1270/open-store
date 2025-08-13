package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import ru.gb.model.User;
import ru.gb.service.UserService;
import ru.gb.service.VerificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationRestControllerTest {

    @Mock
    private VerificationService verificationService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private VerificationRestController controller;

    private final String testEmail = "test@example.com";
    private final String testCode = "123456";
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmailAddress(testEmail);
        testUser.setName("Test");
        testUser.setSurname("User");

        SecurityContextHolder.setContext(securityContext);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void checkEmail_ValidEmailExists() {
        when(verificationService.findUserByEmail(testEmail)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = controller.checkEmail(Map.of("email", testEmail));

        assertEquals(200, response.getStatusCodeValue());
        VerificationRestController.CheckEmailResponse body =
                (VerificationRestController.CheckEmailResponse) response.getBody();
        assertTrue(body.isExists());
        assertEquals("Email topildi.", body.getMessage());
    }

    @Test
    void checkEmail_ValidEmailNotExists() {
        when(verificationService.findUserByEmail(testEmail)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.checkEmail(Map.of("email", testEmail));

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.CheckEmailResponse body =
                (VerificationRestController.CheckEmailResponse) response.getBody();
        assertFalse(body.isExists());
        assertEquals("Email topilmadi.", body.getMessage());
    }

    @Test
    void checkEmail_EmptyEmail() {
        ResponseEntity<?> response = controller.checkEmail(Map.of("email", ""));

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.CheckEmailResponse body =
                (VerificationRestController.CheckEmailResponse) response.getBody();
        assertFalse(body.isExists());
        assertEquals("Email manzili yuborilmadi.", body.getMessage());
    }

    @Test
    void sendSms_Success() {
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(2);
        when(verificationService.requestVerificationCode(eq(testEmail), any(HttpServletRequest.class)))
                .thenReturn(expiryDate);

        ResponseEntity<?> response = controller.sendVerificationCode(Map.of("email", testEmail), request);

        assertEquals(200, response.getStatusCodeValue());
        VerificationRestController.SendSmsResponse body =
                (VerificationRestController.SendSmsResponse) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("Tasdiqlash kodi yuborildi.", body.getMessage());
        assertEquals(expiryDate.toString(), body.getExpiryDate());
    }

    @Test
    void sendSms_Failure() {
        when(verificationService.requestVerificationCode(eq(testEmail), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Request limit exceeded"));

        ResponseEntity<?> response = controller.sendVerificationCode(Map.of("email", testEmail), request);

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.SendSmsResponse body =
                (VerificationRestController.SendSmsResponse) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("Request limit exceeded", body.getMessage());
        assertNull(body.getExpiryDate());
    }

    @Test
    void verifySms_Success() {
        when(verificationService.verifyCode(testEmail, testCode, request)).thenReturn(true);
        when(verificationService.findUserByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userService.loadUserByUsername(testEmail)).thenReturn(userDetails);

        ResponseEntity<?> response = controller.verifyCode(Map.of("email", testEmail, "code", testCode), request);

        assertEquals(200, response.getStatusCodeValue());
        VerificationRestController.VerifySmsResponse body =
                (VerificationRestController.VerifySmsResponse) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("Kod tasdiqlandi, tizimga kirish muvaffaqiyatli.", body.getMessage());

        // Verify security setup
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(session).setAttribute(
                eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                eq(SecurityContextHolder.getContext()));
    }

    @Test
    void verifySms_InvalidCode() {
        when(verificationService.verifyCode(testEmail, testCode, request)).thenReturn(false);

        ResponseEntity<?> response = controller.verifyCode(Map.of("email", testEmail, "code", testCode), request);

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.VerifySmsResponse body =
                (VerificationRestController.VerifySmsResponse) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("Noto‘g‘ri yoki muddati o‘tgan kod.", body.getMessage());


        verifyNoInteractions(userService);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void verifySms_UserNotFound() {
        when(verificationService.verifyCode(testEmail, testCode, request)).thenReturn(true);
        when(verificationService.findUserByEmail(testEmail)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.verifyCode(Map.of("email", testEmail, "code", testCode), request);

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.VerifySmsResponse body =
                (VerificationRestController.VerifySmsResponse) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("Foydalanuvchi topilmadi.", body.getMessage());
    }

    @Test
    void verifySms_MissingParameters() {
        ResponseEntity<?> response = controller.verifyCode(Map.of("email", ""), request);

        assertEquals(400, response.getStatusCodeValue());
        VerificationRestController.VerifySmsResponse body =
                (VerificationRestController.VerifySmsResponse) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("Email yoki kod yuborilmadi.", body.getMessage());
    }
}