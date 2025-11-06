package com.example.appleStore.Service;

import com.example.appleStore.Model.ProductVariants;
import com.example.appleStore.Repository.ProductRepository;
import com.example.appleStore.Repository.ProductVariantsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductVariantsService {

    private final ProductVariantsRepository productVariantRepository;
    private final ProductRepository productRepository;

    public ProductVariantsService(ProductVariantsRepository productVariantRepository, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    public List<ProductVariants> getAllVariants() {
        return productVariantRepository.findAll();
    }

    public Optional<ProductVariants> getVariantById(Long id) {
        return productVariantRepository.findById(id);
    }

    public List<ProductVariants> getVariantsByProductId(Long productId) {
        return productVariantRepository.findByProductId(productId);
    }

    public ProductVariants createVariant(ProductVariants variant) {
        productRepository.findById(variant.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productVariantRepository.save(variant);
    }

    public ProductVariants updateVariant(Long id, ProductVariants updatedVariant) {
        return productVariantRepository.findById(id)
                .map(variant -> {
                    variant.setColor(updatedVariant.getColor());
                    variant.setStorage(updatedVariant.getStorage());
                    variant.setPrice(updatedVariant.getPrice());
                    variant.setQuantity(updatedVariant.getQuantity());
                    variant.setImageUrl(updatedVariant.getImageUrl());
                    return productVariantRepository.save(variant);
                })
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }

    public void deleteVariant(Long id) {
        productVariantRepository.deleteById(id);
    }
}
