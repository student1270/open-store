package ru.gb.controllers;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.gb.JUnitSpringBootBase;
import ru.gb.model.Category;
import ru.gb.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeRestControllerTest extends JUnitSpringBootBase {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private HomeRestController homeRestController;


    @Setter
    @Getter
    static class HomeResponse {
        private String title;
        private List<Category> categories;

    }

    @BeforeEach
    void setUp() {
        // WebTestClient ni kontroller bilan bog'lash
        webTestClient = WebTestClient.bindToController(homeRestController).build();
    }

    @Test
    void testHomePageSuccess() {
        // Ma'lumotlarni tayyorlash
        Category category1 = new Category();
        category1.setId(1L);
        category1.setCategoryName("Electronics");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setCategoryName("Books");

        List<Category> expectedCategories = List.of(category1, category2);

        // Mock behavior
        when(categoryService.findAll()).thenReturn(expectedCategories);

        // Test so'rovi
        HomeResponse responseBody = webTestClient.get()
                .uri("/api/home")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HomeResponse.class)
                .returnResult()
                .getResponseBody();

        // Tekshiruvlar
        assertNotNull(responseBody);
        assertEquals("OpenStore - Bosh sahifa", responseBody.getTitle());
        assertEquals(expectedCategories.size(), responseBody.getCategories().size());
        assertEquals(expectedCategories.get(0).getId(), responseBody.getCategories().get(0).getId());
        assertEquals(expectedCategories.get(0).getCategoryName(), responseBody.getCategories().get(0).getCategoryName());
        assertEquals(expectedCategories.get(1).getId(), responseBody.getCategories().get(1).getId());
        assertEquals(expectedCategories.get(1).getCategoryName(), responseBody.getCategories().get(1).getCategoryName());
    }

    @Test
    void testHomePageEmptyCategories() {
        // Ma'lumotlarni tayyorlash: bo'sh ro'yxat
        when(categoryService.findAll()).thenReturn(List.of());

        // Test so'rovi
        HomeResponse responseBody = webTestClient.get()
                .uri("/api/home")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HomeResponse.class)
                .returnResult()
                .getResponseBody();

        // Tekshiruvlar
        assertNotNull(responseBody);
        assertEquals("OpenStore - Bosh sahifa", responseBody.getTitle());
        assertTrue(responseBody.getCategories().isEmpty());
    }

    @Test
    void testHomePageJsonResponse() {
        // Ma'lumotlarni tayyorlash
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Clothing");

        when(categoryService.findAll()).thenReturn(List.of(category));

        // Test so'rovi
        Map<String, Object> responseBody = webTestClient.get()
                .uri("/api/home")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
                .returnResult()
                .getResponseBody();

        // Tekshiruvlar
        assertNotNull(responseBody);
        assertEquals("OpenStore - Bosh sahifa", responseBody.get("title"));
        List<Map<String, Object>> categories = (List<Map<String, Object>>) responseBody.get("categories");
        assertEquals(1, categories.size());
        assertEquals(1L, categories.get(0).get("id"));
        assertEquals("Clothing", categories.get(0).get("name"));
    }
}