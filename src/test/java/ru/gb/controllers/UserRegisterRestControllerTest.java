package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.model.User;
import ru.gb.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisterRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserRegisterRestController controller;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setEmailAddress("john@example.com");
        testUser.setPhoneNumber("123456789");
    }

    @Test
    void showRegisterPage_ReturnsEmptyUser() {
        ResponseEntity<?> response = controller.showRegisterPage();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.get("user") instanceof User);
    }

    @Test
    void handleRegister_SuccessfulRegistration() throws Exception {

        when(userService.saveUser(any(User.class))).thenReturn(true);


        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);


        SecurityContextHolder.setContext(securityContext);


        when(request.getSession()).thenReturn(session);

        ResponseEntity<?> response = controller.handleRegister(testUser, request);


        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("/home", responseBody.get("redirect"));


        verify(securityContext).setAuthentication(any(Authentication.class));
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any(SecurityContext.class));
    }

    @Test
    void handleRegister_FailedRegistration() {
        when(userService.saveUser(any(User.class))).thenReturn(false);

        ResponseEntity<?> response = controller.handleRegister(testUser, request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Ro'yxatdan o'tishda xatolik yuz berdi", responseBody.get("error"));


        verifyNoInteractions(securityContext);
        verifyNoInteractions(session);
    }

    @Test
    void checkUserDetails_EmailExists() {
        when(userService.isEmailExists("test@example.com")).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> response =
                controller.checkUserDetails("test@example.com", null);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().get("exists"));
    }

    @Test
    void checkUserDetails_PhoneExists() {
        when(userService.isPhoneExists("987654321")).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> response =
                controller.checkUserDetails(null, "987654321");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().get("exists"));
    }

    @Test
    void checkUserDetails_NeitherExists() {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        when(userService.isPhoneExists(anyString())).thenReturn(false);

        ResponseEntity<Map<String, Boolean>> response =
                controller.checkUserDetails("test@example.com", "987654321");

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().get("exists"));
    }

    @Test
    void checkUserDetails_EmptyParams() {
        ResponseEntity<Map<String, Boolean>> response =
                controller.checkUserDetails(null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().get("exists"));
    }
}