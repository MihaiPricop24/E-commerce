package com.example.appleStore;

import com.example.appleStore.Model.*;
import com.example.appleStore.Repository.*;
import com.example.appleStore.Service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private ProductVariant testVariant;
    private Cart testCartItem;
    private Order testOrder;
    private OrderItem testOrderItem;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        // Setup test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Electronics");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductName("iPhone 15 Pro");
        testProduct.setProductDescription("Latest iPhone");
        testProduct.setCategory(testCategory);

        // Setup test variant
        testVariant = new ProductVariant();
        testVariant.setId(1L);
        testVariant.setProduct(testProduct);
        testVariant.setColor("Black");
        testVariant.setStorage("256GB");
        testVariant.setPrice(999.00);
        testVariant.setQuantity(10);

        // Setup test cart item
        testCartItem = new Cart();
        testCartItem.setId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setProductVariant(testVariant);
        testCartItem.setQuantity(2);
        testCartItem.setPriceAtAddition(999.00);
        testCartItem.setAddDate(LocalDateTime.now());

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus("PENDING");
        testOrder.setPrice(1998.00);
        testOrder.setTimeOfOrder(LocalDateTime.now());
        testOrder.setOrderItems(new ArrayList<>());

        // Setup test order item
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProductVariant(testVariant);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(999.00);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        List<Cart> cartItems = Arrays.asList(testCartItem);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testOrderItem));
        when(productVariantRepository.save(any(ProductVariant.class))).thenReturn(testVariant);

        // Act
        Order result = orderService.createOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(1998.00, result.getPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(productVariantRepository, times(1)).save(testVariant);
        verify(cartRepository, times(1)).deleteByUserId(1L);
        assertEquals(8, testVariant.getQuantity()); // 10 - 2 = 8
    }

    @Test
    void createOrder_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_EmptyCart_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(new ArrayList<>());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L);
        });

        assertEquals("Cart is empty. Cannot create Order", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_NullCart_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L);
        });

        assertEquals("Cart is empty. Cannot create Order", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_InsufficientStock_ThrowsException() {
        // Arrange
        testVariant.setQuantity(1); // Less than cart quantity (2)
        List<Cart> cartItems = Arrays.asList(testCartItem);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(cartItems);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L);
        });

        assertEquals("Product Variant Quantity less than Order Quantity", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_MultipleItems_CalculatesCorrectTotal() {
        // Arrange
        Cart cartItem2 = new Cart();
        cartItem2.setId(2L);
        cartItem2.setUser(testUser);

        ProductVariant variant2 = new ProductVariant();
        variant2.setId(2L);
        variant2.setProduct(testProduct);
        variant2.setPrice(500.00);
        variant2.setQuantity(5);

        cartItem2.setProductVariant(variant2);
        cartItem2.setQuantity(3);
        cartItem2.setPriceAtAddition(500.00);

        List<Cart> cartItems = Arrays.asList(testCartItem, cartItem2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList());
        when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Order result = orderService.createOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3498.00, result.getPrice()); // (999 * 2) + (500 * 3) = 3498
        verify(productVariantRepository, times(2)).save(any(ProductVariant.class));
    }

    @Test
    void getOrdersByUser_ReturnsOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getOrdersByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findByUser_IdOrderByTimeOfOrderDesc(1L);
    }

    @Test
    void getOrdersByUser_NoOrders_ReturnsEmptyList() {
        // Arrange
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(new ArrayList<>());

        // Act
        List<Order> result = orderService.getOrdersByUser(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderById_ReturnsOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(testOrder);

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    void getOrderById_NotFound_ReturnsNull() {
        // Arrange
        when(orderRepository.findByIdWithItems(999L)).thenReturn(null);

        // Act
        Order result = orderService.getOrderById(999L);

        // Assert
        assertNull(result);
    }

    @Test
    void calculateOrderTotal_ReturnsCorrectTotal() {
        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setPrice(100.00);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPrice(50.00);
        item2.setQuantity(3);

        List<OrderItem> orderItems = Arrays.asList(item1, item2);

        // Act
        Double total = orderService.calculateOrderTotal(orderItems);

        // Assert
        assertEquals(350.00, total); // (100 * 2) + (50 * 3) = 350
    }

    @Test
    void calculateOrderTotal_EmptyList_ReturnsZero() {
        // Act
        Double total = orderService.calculateOrderTotal(new ArrayList<>());

        // Assert
        assertEquals(0.0, total);
    }

    @Test
    void calculateOrderTotal_NullList_ReturnsZero() {
        // Act
        Double total = orderService.calculateOrderTotal(null);

        // Assert
        assertEquals(0.0, total);
    }

    @Test
    void getTotalSpentByUser_ReturnsCorrectTotal() {
        // Arrange
        Order order1 = new Order();
        order1.setStatus("COMPLETED");
        order1.setPrice(100.00);

        Order order2 = new Order();
        order2.setStatus("PENDING");
        order2.setPrice(200.00);

        Order order3 = new Order();
        order3.setStatus("CANCELLED");
        order3.setPrice(50.00);

        List<Order> orders = Arrays.asList(order1, order2, order3);
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(orders);

        // Act
        Double total = orderService.getTotalSpentByUser(1L);

        // Assert
        assertEquals(300.00, total); // 100 + 200 (cancelled excluded)
    }

    @Test
    void getTotalSpentByUser_NoOrders_ReturnsZero() {
        // Arrange
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(new ArrayList<>());

        // Act
        Double total = orderService.getTotalSpentByUser(1L);

        // Assert
        assertEquals(0.0, total);
    }

    @Test
    void getTotalSpentByUser_AllCancelled_ReturnsZero() {
        // Arrange
        Order order1 = new Order();
        order1.setStatus("CANCELLED");
        order1.setPrice(100.00);

        Order order2 = new Order();
        order2.setStatus("CANCELLED");
        order2.setPrice(200.00);

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(orders);

        // Act
        Double total = orderService.getTotalSpentByUser(1L);

        // Assert
        assertEquals(0.0, total);
    }

    @Test
    void getOrderCount_ReturnsCorrectCount() {
        // Arrange
        Order order1 = new Order();
        order1.setStatus("COMPLETED");

        Order order2 = new Order();
        order2.setStatus("PENDING");

        Order order3 = new Order();
        order3.setStatus("CANCELLED");

        List<Order> orders = Arrays.asList(order1, order2, order3);
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(orders);

        // Act
        Long count = orderService.getOrderCount(1L);

        // Assert
        assertEquals(2L, count); // Only non-cancelled orders
    }

    @Test
    void getOrderCount_NoOrders_ReturnsZero() {
        // Arrange
        when(orderRepository.findByUser_IdOrderByTimeOfOrderDesc(1L)).thenReturn(new ArrayList<>());

        // Act
        Long count = orderService.getOrderCount(1L);

        // Assert
        assertEquals(0L, count);
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        testOrder.getOrderItems().add(testOrderItem);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));
        when(productVariantRepository.save(any(ProductVariant.class))).thenReturn(testVariant);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        assertEquals(12, testVariant.getQuantity()); // Original 10 + returned 2
        verify(orderRepository, times(1)).save(testOrder);
        verify(productVariantRepository, times(1)).save(testVariant);
    }

    @Test
    void cancelOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(999L);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_NotPending_ThrowsException() {
        // Arrange
        testOrder.setStatus("SHIPPED");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("Only Pending orders can be cancelled", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        // Arrange
        testOrder.setStatus("CANCELLED");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("Only Pending orders can be cancelled", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_MultipleItems_RestoresAllStock() {
        // Arrange
        ProductVariant variant2 = new ProductVariant();
        variant2.setId(2L);
        variant2.setProduct(testProduct);
        variant2.setQuantity(5);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(testOrder);
        orderItem2.setProductVariant(variant2);
        orderItem2.setQuantity(3);
        orderItem2.setPrice(500.00);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem, orderItem2));
        when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertEquals("CANCELLED", result.getStatus());
        assertEquals(12, testVariant.getQuantity()); // 10 + 2
        assertEquals(8, variant2.getQuantity()); // 5 + 3
        verify(productVariantRepository, times(2)).save(any(ProductVariant.class));
    }
}