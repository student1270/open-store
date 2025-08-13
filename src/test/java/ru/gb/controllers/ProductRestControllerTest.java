package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductRestControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductRestController productRestController;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Electronics");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("Smartphone");
        testProduct.setPrice(BigDecimal.valueOf(1500));
        testProduct.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getProductsByCategory_Success_DefaultSort() {
        when(productService.findProductsByCategory(eq(1L), any(Sort.class)))
                .thenReturn(List.of(testProduct));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getProductsByCategory(1L, null);

        assertNotNull(response);
        assertEquals(List.of(testProduct), response.get("products"));
        assertEquals(testCategory, response.get("category"));
        assertNull(response.get("sort"));
        assertEquals(List.of(testCategory), response.get("categories"));
    }

    @Test
    void getProductsByCategory_Success_SortArzon() {
        when(productService.findProductsByCategory(eq(1L), any(Sort.class)))
                .thenReturn(List.of(testProduct));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getProductsByCategory(1L, "arzon");

        assertEquals(List.of(testProduct), response.get("products"));
        assertEquals("arzon", response.get("sort"));
    }

    @Test
    void getProductsByCategory_Success_SortQimmat() {
        when(productService.findProductsByCategory(eq(1L), any(Sort.class)))
                .thenReturn(List.of(testProduct));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getProductsByCategory(1L, "qimmat");

        assertEquals(List.of(testProduct), response.get("products"));
        assertEquals("qimmat", response.get("sort"));
    }

    @Test
    void getProductsByCategory_Success_SortYangi() {
        when(productService.findProductsByCategory(eq(1L), any(Sort.class)))
                .thenReturn(List.of(testProduct));
        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getProductsByCategory(1L, "yangi");

        assertEquals(List.of(testProduct), response.get("products"));
        assertEquals("yangi", response.get("sort"));
    }

    @Test
    void getProductsByCategory_InvalidCategoryId() {
        when(productService.findProductsByCategory(eq(1L), any(Sort.class)))
                .thenThrow(new IllegalArgumentException("Invalid category ID"));
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getProductsByCategory(1L, "arzon");

        assertEquals("Noto'g'ri kategoriya ID si.", response.get("message"));
        assertTrue(((List<?>) response.get("products")).isEmpty());
        assertNull(response.get("category"));
        assertNull(response.get("sort"));
        assertEquals(List.of(testCategory), response.get("categories"));
    }


    @Test
    void getAllCategories_Success() {
        when(categoryService.findAll()).thenReturn(List.of(testCategory));

        Map<String, Object> response = productRestController.getAllCategories();

        assertNotNull(response);
        assertEquals(List.of(testCategory), response.get("categories"));
    }
}
