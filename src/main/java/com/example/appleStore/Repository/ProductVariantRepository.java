package com.example.appleStore.Repository;

import com.example.appleStore.Model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {

    List<ProductVariant> findByProductId(Long productId);

    @Query("SELECT v FROM ProductVariant v WHERE v.id = :id AND v.quantity > 0")
    ProductVariant findByIdIfInStock(@Param("id") Long id);

}
