package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.gb.model.*;
import ru.gb.repository.CartRepository;
import ru.gb.service.CartProductService;
import ru.gb.service.CartService;
import ru.gb.service.impl.UserDetailsImpl;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartRestControllerTest {

    @Mock private CartService cartService;
    @Mock private CartRepository cartRepository;
    @Mock private CartProductService cartProductService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private HttpSession session;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private CartRestController cartRestController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }


    private User createTestUser(Long id, Roles role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private CartProduct createCartProduct(Long productId, int quantity) {
        Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(1000));

        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setQuantity(quantity);
        return cartProduct;
    }

    private void setupAuthenticatedUser(Roles role) {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        User user = createTestUser(1L, role);
        when(userDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }


    @Test
    void addToCart_Success() {
        when(cartService.addToCart(1L, 2, session)).thenReturn(true);

        ResponseEntity<?> response = cartRestController.addToCart(1L, 2, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mahsulot savatga qo‘shildi", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void addToCart_InvalidQuantity() {
        ResponseEntity<?> response = cartRestController.addToCart(1L, 0, session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Miqdor 1 dan kam bo‘lmasligi kerak", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void addToCart_NotEnoughStock() {
        when(cartService.addToCart(1L, 2, session)).thenReturn(false);

        ResponseEntity<?> response = cartRestController.addToCart(1L, 2, session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Omborda yetarli mahsulot mavjud emas", ((Map<?, ?>) response.getBody()).get("error"));
    }


    @Test
    void viewCart_AnonymousUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        List<CartProduct> mockItems = Collections.singletonList(createCartProduct(1L, 2));
        when(cartService.getCartItems(session)).thenReturn(mockItems);
        when(cartService.getTotalQuantity(session)).thenReturn(2);
        when(cartService.getTotalPrice(session)).thenReturn(BigDecimal.valueOf(2000));


        ResponseEntity<?> response = cartRestController.viewCart(session, null);


        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(mockItems, body.get("items"));
        assertEquals(2, body.get("totalQuantity"));
        assertEquals(BigDecimal.valueOf(2000), body.get("totalPrice"));
    }

    @Test
    void viewCart_AuthenticatedUser_WithSessionCart() {
        setupAuthenticatedUser(Roles.USER);

        Cart sessionCart = new Cart();
        sessionCart.setItems(Collections.singletonList(createCartProduct(1L, 2)));
        when(session.getAttribute("cart")).thenReturn(sessionCart);

        Cart dbCart = new Cart();
        dbCart.setUserId(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(dbCart));

        List<CartProduct> mockItems = Collections.singletonList(createCartProduct(1L, 2));
        when(cartService.getCartItems(session)).thenReturn(mockItems);
        when(cartService.getTotalQuantity(session)).thenReturn(2);
        when(cartService.getTotalPrice(session)).thenReturn(BigDecimal.valueOf(2000));

        ResponseEntity<?> response = cartRestController.viewCart(session, authentication);

        assertEquals(200, response.getStatusCodeValue());
        verify(cartProductService).addOrUpdateCartProduct(eq(dbCart), any(Product.class), eq(2));
        verify(session).removeAttribute("cart");
    }


    @Test
    void updateQuantity_Success() {
        ResponseEntity<?> response = cartRestController.updateQuantity(1L, 3, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Miqdor yangilandi", ((Map<?, ?>) response.getBody()).get("message"));
        verify(cartService).updateQuantity(1L, 3, session);
    }

    @Test
    void updateQuantity_InvalidQuantity() {
        ResponseEntity<?> response = cartRestController.updateQuantity(1L, 0, session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Miqdor 1 dan kam bo‘lmasligi kerak", ((Map<?, ?>) response.getBody()).get("error"));
    }


    @Test
    void removeItem_Success() {
        ResponseEntity<?> response = cartRestController.removeItem(1L, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mahsulot olib tashlandi", ((Map<?, ?>) response.getBody()).get("message"));
        verify(cartService).removeProduct(1L, session);
    }

    @Test
    void clearCart_Success() {
        ResponseEntity<?> response = cartRestController.clearCart(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Savat tozalandi", ((Map<?, ?>) response.getBody()).get("message"));
        verify(cartService).clearCart(session);
    }


    @Test
    void checkout_Unauthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        ResponseEntity<?> response = cartRestController.checkout(session);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Xaridni amalga oshirish uchun tizimga kiring!", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void checkout_NotUserRole() {
        setupAuthenticatedUser(Roles.ORDER_ADMIN);

        ResponseEntity<?> response = cartRestController.checkout(session);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Faqat oddiy foydalanuvchilar xarid qilishi mumkin.", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void checkout_Success() {
        setupAuthenticatedUser(Roles.USER);

        ResponseEntity<?> response = cartRestController.checkout(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Checkout sahifasi", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void confirmPurchase_Success() {
        setupAuthenticatedUser(Roles.USER);

        Order mockOrder = new Order();
        mockOrder.setId(1L);
        when(cartService.confirmPurchase(session)).thenReturn(mockOrder);

        ResponseEntity<?> response = cartRestController.confirmPurchase(session);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Xarid muvaffaqiyatli amalga oshirildi!", body.get("message"));
        assertEquals(1L, body.get("orderId"));
        verify(messagingTemplate).convertAndSend("/topic/warehouse-orders", mockOrder);
    }

    @Test
    void confirmPurchase_Failure() {
        setupAuthenticatedUser(Roles.USER);
        when(cartService.confirmPurchase(session)).thenReturn(null);

        ResponseEntity<?> response = cartRestController.confirmPurchase(session);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Xaridni amalga oshirishda xatolik!", ((Map<?, ?>) response.getBody()).get("error"));
    }


    @Test
    void cancelPurchase_Success() {
        ResponseEntity<?> response = cartRestController.cancelPurchase();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Xarid bekor qilindi!", ((Map<?, ?>) response.getBody()).get("message"));
    }
}