package ru.gb.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.gb.model.Category;
import ru.gb.service.CategoryService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeRestControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private HomeRestController homeRestController;

    @Test
    void homePage_ReturnsCorrectResponse() {

        Category category1 = new Category(1L, "Elektronika");
        Category category2 = new Category(2L, "Kiyim");
        List<Category> mockCategories = Arrays.asList(category1, category2);


        when(categoryService.findAll()).thenReturn(mockCategories);


        ResponseEntity<?> response = homeRestController.homePage();


        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());


        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertEquals("OpenStore - Bosh sahifa", responseBody.get("title"));

        assertTrue(responseBody.get("categories") instanceof List);
        List<Category> returnedCategories = (List<Category>) responseBody.get("categories");
        assertEquals(2, returnedCategories.size());
        assertEquals("Elektronika", returnedCategories.get(0).getCategoryName());


        verify(categoryService, times(1)).findAll();
    }

    @Test
    void homePage_ReturnsEmptyCategoriesList() {

        when(categoryService.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = homeRestController.homePage();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("OpenStore - Bosh sahifa", responseBody.get("title"));
        assertTrue(((List<?>) responseBody.get("categories")).isEmpty());
    }
}