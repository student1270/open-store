package ru.gb.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.gb.model.User;
import ru.gb.service.impl.UserDetailsImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileRestControllerTest {

    @InjectMocks
    private UserProfileRestController controller;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void showUserProfile_AuthenticatedUser_ReturnsUserDetails() {
        // Setup test data
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("John");
        testUser.setSurname("Doe");

        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);

        // Mock authentication - faqat kerakli mocklarni sozlaymiz
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);

        // Test
        ResponseEntity<?> response = controller.showUserProfile(authentication);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("John Doe", responseBody.get("userName"));
        assertEquals(1L, responseBody.get("userId"));

        // Kerakli mocklarni tekshiramiz
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
    }

    @Test
    void showUserProfile_NotAuthenticated_ReturnsUnauthorized() {
        // Mock unauthenticated user
        when(authentication.isAuthenticated()).thenReturn(false);

        // Test
        ResponseEntity<?> response = controller.showUserProfile(authentication);

        // Verify
        assertEquals(401, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Foydalanuvchi autentifikatsiya qilinmagan", responseBody.get("error"));
    }

    @Test
    void showUserProfile_NullAuthentication_ReturnsUnauthorized() {
        // Test with null authentication
        ResponseEntity<?> response = controller.showUserProfile(null);

        // Verify
        assertEquals(401, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Foydalanuvchi autentifikatsiya qilinmagan", responseBody.get("error"));
    }
}