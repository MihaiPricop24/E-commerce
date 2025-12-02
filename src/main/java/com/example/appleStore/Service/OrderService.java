package com.example.appleStore.Service;

import com.example.appleStore.Model.*;
import com.example.appleStore.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = cartRepository.findByUserId(userId);

        if(cartItems == null || cartItems.isEmpty()){
            throw new RuntimeException("Cart is empty. Cannot create Order");
        }

        for(Cart cartItem : cartItems){
            ProductVariant productVariant = cartItem.getProductVariant();
            if(productVariant.getQuantity() < cartItem.getQuantity()){
                throw new RuntimeException("Product Variant Quantity less than Order Quantity");
            }
        }

        Double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPriceAtAddition() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setPrice(totalPrice);
        order.setTimeOfOrder(LocalDateTime.now());
        order.setStatus("PENDING");

        order =  orderRepository.save(order);

        final Order saveOrder = order;
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(saveOrder);
                    orderItem.setProductVariant(cartItem.getProductVariant());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPriceAtAddition());

                    return orderItem;
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        for(Cart cartItem : cartItems){
            ProductVariant productVariant = cartItem.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() - cartItem.getQuantity());
            productVariantRepository.save(productVariant);
        }

        cartRepository.deleteByUserId(userId);

        return order;
    }

    public List<Order> getOrdersByUser(Long userId){
        return orderRepository.findByUser_IdOrderByTimeOfOrderDesc(userId);
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findByIdWithItems(orderId);
    }

    public Double calculateOrderTotal(List<OrderItem> orderItems){
        if(orderItems == null || orderItems.isEmpty()){
            return 0.0;
        }

        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public Double getTotalSpentByUser(Long userId){
        List<Order> orders = orderRepository.findByUser_IdOrderByTimeOfOrderDesc(userId);

        return orders.stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .mapToDouble(Order::getPrice)
                .sum();
    }

    public Long getOrderCount(Long userId){
        List<Order> orders = orderRepository.findByUser_IdOrderByTimeOfOrderDesc(userId);

        return orders.stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .count();
    }

    @Transactional
    public Order cancelOrder(Long orderId){
       Order order = orderRepository.findById(orderId)
               .orElseThrow(() -> new RuntimeException("Order not found"));

       if(!"PENDING".equals(order.getStatus())){
           throw new RuntimeException("Only Pending orders can be cancelled");
       }

       List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
       for(OrderItem orderItem : orderItems){
           ProductVariant productVariant = orderItem.getProductVariant();
           productVariant.setQuantity(productVariant.getQuantity() + orderItem.getQuantity());
           productVariantRepository.save(productVariant);
       }

       order.setStatus("CANCELLED");
       return orderRepository.save(order);
    }
}
