package com.example.appleStore.Service;

import com.example.appleStore.Model.ProductVariant;
import com.example.appleStore.Repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public ProductVariant findById(long id) {
        return productVariantRepository.findById(id).orElse(null);
    }
}
