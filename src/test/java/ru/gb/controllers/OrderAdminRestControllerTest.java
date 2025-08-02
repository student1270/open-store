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

        // Faqat kerakli mockni sozlash
        when(productService.saveProduct(any(Product.class), any(MultipartFile.class), anyString())).thenReturn(true);

        // Test qilish
        ResponseEntity<?> response = orderAdminRestController.addProduct(
                "Test Product",
                "100.50",
                "10",
                "Test description",
                mockFile,
                "Electronics:Mobile"
        );

        // Natijalarni tekshirish
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mahsulot muvaffaqiyatli qo'shildi!", ((Map<?, ?>) response.getBody()).get("message"));

        // Servislar chaqirilganligini tekshirish
        verify(productService).saveProduct(any(Product.class), any(MultipartFile.class), anyString());
    }

    @Test
    void addProduct_Failure() throws Exception {
        // Mock fayl yaratish
        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Faqat kerakli mockni sozlash
        when(productService.saveProduct(any(Product.class), any(MultipartFile.class), anyString())).thenReturn(false);

        // Test qilish
        ResponseEntity<?> response = orderAdminRestController.addProduct(
                "Test Product",
                "100.50",
                "10",
                "Test description",
                mockFile,
                "Electronics:Mobile"
        );

        // Natijalarni tekshirish
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Mahsulot qo'shishda xatolik yuz berdi!", ((Map<?, ?>) response.getBody()).get("error"));

        // Servis chaqirilganligini tekshirish
        verify(productService).saveProduct(any(Product.class), any(MultipartFile.class), anyString());
    }

    @Test
    void addProduct_InvalidPrice() throws Exception {
        // Mock fayl yaratish
        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        try {
            // Noto'g'ri narx formati bilan test qilish
            ResponseEntity<?> response = orderAdminRestController.addProduct(
                    "Test Product",
                    "invalid_price", // Noto'g'ri narx formati
                    "10",
                    "Test description",
                    mockFile,
                    "Electronics:Mobile"
            );

            // Agar exception tashlanmasa, test o'tkazilmadi deb hisoblaymiz
            fail("NumberFormatException kutilyapti, lekin tashlanmadi");
        } catch (NumberFormatException e) {
            // Exception kutilganidek tashlandi, test muvaffaqiyatli
            assertTrue(e.getMessage().contains("Character i is neither a decimal digit number"));
        }
    }
    @Test
    void addProduct_InvalidQuantity() {
        // Mock fayl yaratish
        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Test qilish va exception kutish
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            orderAdminRestController.addProduct(
                    "Test Product",
                    "100.50",
                    "invalid_quantity", // Noto'g'ri miqdor formati
                    "Test description",
                    mockFile,
                    "Electronics:Mobile"
            );
        });

        // Exception xabarini tekshirish (agar kerak bo'lsa)
        assertTrue(exception.getMessage().contains("For input string: \"invalid_quantity\""));
    }
}