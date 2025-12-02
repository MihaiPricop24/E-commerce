package com.example.appleStore;

import com.example.appleStore.Model.*;
import com.example.appleStore.Repository.CartRepository;
import com.example.appleStore.Repository.ProductVariantRepository;
import com.example.appleStore.Repository.UserRepository;
import com.example.appleStore.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private ProductVariant testVariant;
    private Cart testCartItem;
    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Setup test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Electronics");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductName("Test Product");
        testProduct.setProductDescription("Test Description");
        testProduct.setCategory(testCategory);

        // Setup test variant
        testVariant = new ProductVariant();
        testVariant.setId(1L);
        testVariant.setProduct(testProduct);
        testVariant.setPrice(100.00);
        testVariant.setQuantity(10);
        testVariant.setColor("Black");
        testVariant.setStorage("256GB");

        // Setup test cart item
        testCartItem = new Cart();
        testCartItem.setId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setProductVariant(testVariant);
        testCartItem.setQuantity(2);
        testCartItem.setPriceAtAddition(100.00);
        testCartItem.setAddDate(LocalDateTime.now());
        testCartItem.setUpdateDate(LocalDateTime.now());
    }

    @Test
    void getCartByUser_ReturnsCartItems() {
        // Arrange
        List<Cart> expectedCart = Arrays.asList(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(expectedCart);

        // Act
        List<Cart> result = cartService.getCartByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCartItem, result.get(0));
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void addtoCart_NewItem_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(cartRepository.findByUserIdAndProductVariantId(1L, 1L)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCartItem);

        // Act
        Cart result = cartService.addtoCart(1L, 1L, 2);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).findById(1L);
        verify(productVariantRepository, times(1)).findById(1L);
    }

    @Test
    void addtoCart_ExistingItem_UpdatesQuantity() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(cartRepository.findByUserIdAndProductVariantId(1L, 1L)).thenReturn(testCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCartItem);

        // Act
        Cart result = cartService.addtoCart(1L, 1L, 3);

        // Assert
        assertNotNull(result);
        assertEquals(5, testCartItem.getQuantity()); // Original 2 + new 3
        verify(cartRepository, times(1)).save(testCartItem);
    }

    @Test
    void addtoCart_NullUserId_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(null, 1L, 2);
        });

        assertEquals("Invalid input parameters", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_NullVariantId_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(1L, null, 2);
        });

        assertEquals("Invalid input parameters", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_NullQuantity_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(1L, 1L, null);
        });

        assertEquals("Invalid input parameters", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(999L, 1L, 2);
        });

        assertEquals("User not found", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_ProductVariantNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productVariantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(1L, 999L, 2);
        });

        assertEquals("Product Variant not found", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_InsufficientStock_ThrowsException() {
        // Arrange
        testVariant.setQuantity(1); // Less than requested quantity (2)
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(1L, 1L, 2);
        });

        assertTrue(exception.getMessage().contains("Not enough stock available"));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addtoCart_ExistingItem_InsufficientStockForNewQuantity_ThrowsException() {
        // Arrange
        testVariant.setQuantity(3); // Total available
        testCartItem.setQuantity(2); // Already in cart
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(cartRepository.findByUserIdAndProductVariantId(1L, 1L)).thenReturn(testCartItem);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addtoCart(1L, 1L, 2); // Trying to add 2 more (total would be 4)
        });

        assertTrue(exception.getMessage().contains("Cannot add more items"));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_Success() {
        // Arrange
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCartItem);

        // Act
        Cart result = cartService.updateCartItem(1L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(5, testCartItem.getQuantity());
        verify(cartRepository, times(1)).save(testCartItem);
    }

    @Test
    void updateCartItem_NullQuantity_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartItem(1L, null);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_ZeroQuantity_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartItem(1L, 0);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_CartNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartItem(999L, 5);
        });

        assertEquals("Cart item not found", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_InsufficientStock_ThrowsException() {
        // Arrange
        testVariant.setQuantity(3); // Only 3 available
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartItem(1L, 5); // Trying to update to 5
        });

        assertTrue(exception.getMessage().contains("Not enough stock available"));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeFromCart_Success() {
        // Arrange
        when(cartRepository.existsById(1L)).thenReturn(true);

        // Act
        cartService.removeFromCart(1L);

        // Assert
        verify(cartRepository, times(1)).deleteById(1L);
    }

    @Test
    void removeFromCart_ItemNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.removeFromCart(999L);
        });

        assertEquals("Cart item not found", exception.getMessage());
        verify(cartRepository, never()).deleteById(anyLong());
    }

    @Test
    void clearCart_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartRepository, times(1)).deleteByUserId(1L);
    }

    @Test
    void clearCart_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.clearCart(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(cartRepository, never()).deleteByUserId(anyLong());
    }

    @Test
    void calculateTotal_ReturnsCorrectTotal() {
        // Arrange
        Cart cartItem1 = new Cart();
        cartItem1.setPriceAtAddition(100.00);
        cartItem1.setQuantity(2);

        Cart cartItem2 = new Cart();
        cartItem2.setPriceAtAddition(50.00);
        cartItem2.setQuantity(3);

        List<Cart> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartRepository.findByUserId(1L)).thenReturn(cartItems);

        // Act
        Double total = cartService.calculateTotal(1L);

        // Assert
        assertEquals(350.00, total); // (100 * 2) + (50 * 3) = 350
    }

    @Test
    void calculateTotal_EmptyCart_ReturnsZero() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Act
        Double total = cartService.calculateTotal(1L);

        // Assert
        assertEquals(0.00, total);
    }

    @Test
    void getCartItemCount_ReturnsCorrectCount() {
        // Arrange
        Cart cartItem1 = new Cart();
        cartItem1.setQuantity(2);

        Cart cartItem2 = new Cart();
        cartItem2.setQuantity(3);

        List<Cart> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartRepository.findByUserId(1L)).thenReturn(cartItems);

        // Act
        Integer count = cartService.getCartItemCount(1L);

        // Assert
        assertEquals(5, count); // 2 + 3 = 5
    }

    @Test
    void getCartItemCount_EmptyCart_ReturnsZero() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Act
        Integer count = cartService.getCartItemCount(1L);

        // Assert
        assertEquals(0, count);
    }
}