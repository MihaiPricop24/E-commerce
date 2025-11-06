package com.example.appleStore.Repository;

import com.example.appleStore.Model.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantsRepository extends JpaRepository<ProductVariants, Long> {
    List<ProductVariants> findByProductId(Long productId);
}
