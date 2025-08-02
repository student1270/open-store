package ru.gb.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ImageService;
import ru.gb.service.ProductService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAdminRestControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ImageService imageService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private OrderAdminRestController orderAdminRestController;

    @Test
    void showAdminPage_ReturnsCorrectResponse() {
        ResponseEntity<?> response = orderAdminRestController.showAdminPage();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin Paneli", ((Map<?, ?>) response.getBody()).get("title"));
    }

    @Test
    void showAddProductPage_ReturnsCorrectResponse() {
        ResponseEntity<?> response = orderAdminRestController.showAddProductPage();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mahsulot Qo‘shish", ((Map<?, ?>) response.getBody()).get("title"));
    }

    @Test
    void showStoredOrdersPage_ReturnsCorrectResponse() {
        ResponseEntity<?> response = orderAdminRestController.showStoredOrdersPage();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Saqlanayotgan Buyurtmalar", ((Map<?, ?>) response.getBody()).get("title"));
    }

    @Test
    void addProduct_Success() throws Exception {
        // Mock fayl yaratish
        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );


        when(productService.saveProduct(any(Product.class), any(MultipartFile.class), anyString())).thenReturn(true);


        ResponseEntity<?> response = orderAdminRestController.addProduct(
                "Test Product",
                "100.50",
                "10",
                "Test description",
                mockFile,
                "Electronics:Mobile"
        );


        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mahsulot muvaffaqiyatli qo'shildi!", ((Map<?, ?>) response.getBody()).get("message"));


        verify(productService).saveProduct(any(Product.class), any(MultipartFile.class), anyString());
    }

    @Test
    void addProduct_Failure() throws Exception {

        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );


        when(productService.saveProduct(any(Product.class), any(MultipartFile.class), anyString())).thenReturn(false);


        ResponseEntity<?> response = orderAdminRestController.addProduct(
                "Test Product",
                "100.50",
                "10",
                "Test description",
                mockFile,
                "Electronics:Mobile"
        );


        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Mahsulot qo'shishda xatolik yuz berdi!", ((Map<?, ?>) response.getBody()).get("error"));


        verify(productService).saveProduct(any(Product.class), any(MultipartFile.class), anyString());
    }

    @Test
    void addProduct_InvalidPrice() throws Exception {

        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        try {

            ResponseEntity<?> response = orderAdminRestController.addProduct(
                    "Test Product",
                    "invalid_price",
                    "10",
                    "Test description",
                    mockFile,
                    "Electronics:Mobile"
            );


            fail("NumberFormatException kutilyapti, lekin tashlanmadi");
        } catch (NumberFormatException e) {

            assertTrue(e.getMessage().contains("Character i is neither a decimal digit number"));
        }
    }
    @Test
    void addProduct_InvalidQuantity() {

        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );


        Exception exception = assertThrows(NumberFormatException.class, () -> {
            orderAdminRestController.addProduct(
                    "Test Product",
                    "100.50",
                    "invalid_quantity",
                    "Test description",
                    mockFile,
                    "Electronics:Mobile"
            );
        });


        assertTrue(exception.getMessage().contains("For input string: \"invalid_quantity\""));
    }
}