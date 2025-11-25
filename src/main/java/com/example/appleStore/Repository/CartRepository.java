package com.example.appleStore.Repository;

import com.example.appleStore.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long id);

    Cart findByUserIdAndProductVariantId(Long userId, Long variantId);

    void deleteByUserId(Long userId);
}
