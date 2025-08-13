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

        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("John");
        testUser.setSurname("Doe");

        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);


        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);


        ResponseEntity<?> response = controller.showUserProfile(authentication);


        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("John Doe", responseBody.get("userName"));
        assertEquals(1L, responseBody.get("userId"));


        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
    }

    @Test
    void showUserProfile_NotAuthenticated_ReturnsUnauthorized() {

        when(authentication.isAuthenticated()).thenReturn(false);


        ResponseEntity<?> response = controller.showUserProfile(authentication);


        assertEquals(401, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Foydalanuvchi autentifikatsiya qilinmagan", responseBody.get("error"));
    }

    @Test
    void showUserProfile_NullAuthentication_ReturnsUnauthorized() {

        ResponseEntity<?> response = controller.showUserProfile(null);


        assertEquals(401, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Foydalanuvchi autentifikatsiya qilinmagan", responseBody.get("error"));
    }
}