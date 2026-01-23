package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.gb.model.Product;
import ru.gb.model.Review;
import ru.gb.model.User;
import ru.gb.service.ProductService;
import ru.gb.service.ReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailRestControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ProductDetailRestController controller;

    private Product testProduct;
    private Review testReview1;
    private Review testReview2;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John");
        testUser.setSurname("Doe");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.50));
        testProduct.setRating(4.5);

        testReview1 = new Review();
        testReview1.setId(1L);
        testReview1.setUser(testUser);
        testReview1.setProduct(testProduct);
        testReview1.setCommentText("Great product!");
        testReview1.setRating(5);
        testReview1.setLocalDateTime(LocalDateTime.now());

        testReview2 = new Review();
        testReview2.setId(2L);
        testReview2.setUser(testUser);
        testReview2.setProduct(testProduct);
        testReview2.setCommentText("Not bad");
        testReview2.setRating(4);
        testReview2.setLocalDateTime(LocalDateTime.now().minusDays(1));
    }

    @Test
    void getProductById_SuccessWithReviews() {

        when(productService.findProductsById(1L)).thenReturn(testProduct);
        when(reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(1L))
                .thenReturn(List.of(testReview1, testReview2));


        ResponseEntity<?> response = controller.getProductById(1L);


        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(testProduct, responseBody.get("productDetailPage"));

        List<Review> reviews = (List<Review>) responseBody.get("latestTwoReviews");
        assertEquals(2, reviews.size());
        assertEquals("Great product!", reviews.get(0).getCommentText());
    }

    @Test
    void getProductById_ProductNotFound() {
        when(productService.findProductsById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.getProductById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Mahsulot topilmadi", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void getProductById_EmptyReviews() {
        when(productService.findProductsById(1L)).thenReturn(testProduct);
        when(reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(1L))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.getProductById(1L);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(0, ((List<?>) responseBody.get("latestTwoReviews")).size());
    }

    @Test
    void getProductById_InternalServerError() {
        when(productService.findProductsById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.getProductById(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Ichki server xatosi", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void findReviewsPage_SuccessWithoutFeedbackId() {
        when(reviewService.findReviewsByProductIdDesc(1L))
                .thenReturn(List.of(testReview1, testReview2));

        ResponseEntity<?> response = controller.findReviewsPage(1L, null);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, ((List<?>) responseBody.get("reviews")).size());
        assertEquals(1L, responseBody.get("productId"));
        assertFalse(responseBody.containsKey("highlightFeedbackId"));
    }

    @Test
    void findReviewsPage_SuccessWithFeedbackId() {
        when(reviewService.findReviewsByProductIdDesc(1L))
                .thenReturn(List.of(testReview1, testReview2));

        ResponseEntity<?> response = controller.findReviewsPage(1L, 2L);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2L, responseBody.get("highlightFeedbackId"));
    }

    @Test
    void findReviewsPage_EmptyReviews() {
        when(reviewService.findReviewsByProductIdDesc(1L))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.findReviewsPage(1L, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, ((List<?>) ((Map<?, ?>) response.getBody()).get("reviews")).size());
    }

    @Test
    void findReviewsPage_InternalServerError() {
        when(reviewService.findReviewsByProductIdDesc(1L))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.findReviewsPage(1L, null);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Ichki server xatosi", ((Map<?, ?>) response.getBody()).get("error"));
    }
}