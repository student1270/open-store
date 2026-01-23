package ru.gb.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ru.gb.model.Admin;
import ru.gb.model.Roles;
import ru.gb.service.impl.AdminDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseAdminRestControllerTest {

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WarehouseAdminRestController controller;

    @Test
    void getWarehouseAdmin_ReturnsAdmin_WhenAuthenticated() {

        Admin testAdmin = new Admin();
        testAdmin.setId(1);
        testAdmin.setUsername("warehouse_admin");
        testAdmin.setPassword("securepass");
        testAdmin.setRole(Roles.WAREHOUSE_ADMIN);

        AdminDetails adminDetails = new AdminDetails(testAdmin);


        when(authentication.getPrincipal()).thenReturn(adminDetails);


        Admin result = controller.getWarehouseAdmin(authentication);


        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("warehouse_admin", result.getUsername());
        assertEquals(Roles.WAREHOUSE_ADMIN, result.getRole());
        verify(authentication).getPrincipal();
    }

    @Test
    void getWarehouseAdmin_ThrowsException_WhenPrincipalIsNotAdminDetails() {

        when(authentication.getPrincipal()).thenReturn("not_admin_details");


        assertThrows(ClassCastException.class, () -> {
            controller.getWarehouseAdmin(authentication);
        });
    }

    @Test
    void getWarehouseAdmin_ThrowsException_WhenNotAuthenticated() {

        when(authentication.getPrincipal()).thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            controller.getWarehouseAdmin(authentication);
        });
    }
}