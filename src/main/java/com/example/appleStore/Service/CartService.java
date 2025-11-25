package com.example.appleStore.Service;

import com.example.appleStore.Model.Cart;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Model.ProductVariant;
import com.example.appleStore.Model.User;
import com.example.appleStore.Repository.CartRepository;
import com.example.appleStore.Repository.ProductVariantRepository;
import com.example.appleStore.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductVariantRepository productVariantRepository,  UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    public List<Cart> getCartByUser(long userId){
        return cartRepository.findByUserId(userId);
    }

    @Transactional
    public Cart addtoCart(Long userId, Long variantId, Integer quantity){
        if(userId == null || variantId == null || quantity == null){
            throw new RuntimeException("Invalid input parameters");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product Variant not found"));

        if(productVariant.getQuantity() < quantity){
            throw new RuntimeException("Not enough stock available. " +
                    productVariant.getQuantity() + "items left");
        }

        Cart existingcartItem = cartRepository.findByUserIdAndProductVariantId(userId, variantId);

        if(existingcartItem != null){
            int newQuantity = existingcartItem.getQuantity() + quantity;

            if(productVariant.getQuantity() < newQuantity){
                throw new RuntimeException("Cannot add more items. " +
                        productVariant.getQuantity() + "items left");
            }
            existingcartItem.setQuantity(newQuantity);
            existingcartItem.setUpdateDate(LocalDateTime.now());
            return cartRepository.save(existingcartItem);
        } else {
         Cart newCartItem = new Cart();
         newCartItem.setUser(user);
         newCartItem.setProductVariant(productVariant);
         newCartItem.setQuantity(quantity);
         newCartItem.setPriceAtAddition(productVariant.getPrice());
         newCartItem.setAddDate(LocalDateTime.now());
         newCartItem.setUpdateDate(LocalDateTime.now());

         return cartRepository.save(newCartItem);
        }
    }

    @Transactional
    public Cart updateCartItem(Long cartId, Integer quantity){
        if(quantity == null || quantity <= 0){
            throw new RuntimeException("Quantity must be greater than zero");
        }

        Cart cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        ProductVariant productVariant = cartItem.getProductVariant();
        if(productVariant.getQuantity() < quantity){
            throw new RuntimeException("Not enough stock available. " +
                    productVariant.getQuantity() + "items available");
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdateDate(LocalDateTime.now());

        return cartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long cartId){
        if(!cartRepository.existsById(cartId)){
            throw new RuntimeException("Cart item not found");
        }

        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartRepository.deleteByUserId(userId);
    }

    public Double calculateTotal(Long userId){
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream()
                .mapToDouble(item -> item.getPriceAtAddition() * item.getQuantity())
                .sum();
    }

    public Integer getCartItemCount(Long userId){
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream()
                .mapToInt( Cart::getQuantity)
                .sum();
    }
}
