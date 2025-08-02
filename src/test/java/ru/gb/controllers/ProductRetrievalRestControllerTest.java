package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ProductRetrievalService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRetrievalRestControllerTest {

    @Mock
    private ProductRetrievalService productRetrievalService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductRetrievalRestController controller;

    private Product product1;
    private Product product2;
    private Category testCategory;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        testCategory = new Category(1L, "Electronics");

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Smartphone");
        product1.setPrice(BigDecimal.valueOf(500));
        product1.setCategory(testCategory);
        product1.setCreatedAt(LocalDateTime.now());

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Laptop");
        product2.setPrice(BigDecimal.valueOf(1000));
        product2.setCategory(testCategory);
        product2.setCreatedAt(LocalDateTime.now().minusDays(1));

        categories = Arrays.asList(
                new Category(1L, "Electronics"),
                new Category(2L, "Clothing"),
                new Category(3L, "Books")
        );
    }

    @Test
    void getProductsByCategory_DefaultSort() {
        // Mock sozlamalari
        when(productRetrievalService.findProductsByCategory(1L))
                .thenReturn(Arrays.asList(product1, product2));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(categories);

        // Test qilish
        ResponseEntity<?> response = controller.getProductsByCategory(1L, null);

        // Natijalarni tekshirish
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, ((List<?>) responseBody.get("products")).size());
        assertEquals(testCategory, responseBody.get("category"));
        assertNull(responseBody.get("sort"));
        assertEquals(3, ((List<?>) responseBody.get("categories")).size());
    }

    @Test
    void getProductsByCategory_PriceAscSort() {
        // Mock sozlamalari
        when(productRetrievalService.findProductsByCategoryAndPriceAsc(1L))
                .thenReturn(Arrays.asList(product1, product2));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(categories);

        // Test qilish
        ResponseEntity<?> response = controller.getProductsByCategory(1L, "arzon");

        // Natijalarni tekshirish
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("arzon", responseBody.get("sort"));
    }

    @Test
    void getProductsByCategory_PriceDescSort() {
        // Mock sozlamalari
        when(productRetrievalService.findProductsByCategoryAndPriceDesc(1L))
                .thenReturn(Arrays.asList(product2, product1));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(categories);

        // Test qilish
        ResponseEntity<?> response = controller.getProductsByCategory(1L, "qimmat");

        // Natijalarni tekshirish
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("qimmat", responseBody.get("sort"));
    }

    @Test
    void getProductsByCategory_EmptyProducts() {
        // Mock sozlamalari
        when(productRetrievalService.findProductsByCategory(1L))
                .thenReturn(Collections.emptyList());
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(categories);

        // Test qilish
        ResponseEntity<?> response = controller.getProductsByCategory(1L, null);

        // Natijalarni tekshirish
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(0, ((List<?>) responseBody.get("products")).size());
    }

    @Test
    void getProductsByCategory_InvalidCategory() {
        // Mock sozlamalari
        when(productRetrievalService.findProductsByCategory(999L))
                .thenThrow(new IllegalArgumentException("Noto'g'ri kategoriya ID si."));
        when(categoryService.findAll()).thenReturn(categories);

        // Test qilish
        ResponseEntity<?> response = controller.getProductsByCategory(999L, null);

        // Natijalarni tekshirish
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Noto'g'ri kategoriya ID si.", responseBody.get("message"));
        assertEquals(0, ((List<?>) responseBody.get("products")).size());
        assertNull(responseBody.get("category"));
        assertEquals(3, ((List<?>) responseBody.get("categories")).size());
    }

    @Test
    void redirectToHome_ShouldReturn302() {
        ResponseEntity<?> response = controller.redirectToHome();

        assertEquals(302, response.getStatusCodeValue());
        assertEquals("/api/home", response.getHeaders().get("Location").get(0));
    }
}