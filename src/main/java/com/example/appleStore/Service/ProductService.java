package com.example.appleStore.Service;

import com.example.appleStore.DTO.ProductFilterDTO;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Model.ProductVariant;
import com.example.appleStore.Repository.CategoryRepository;
import com.example.appleStore.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public  ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductByCategory(Long id){
        return productRepository.findByCategory_Id(id);
    }

    public List<Product> filterProducts(ProductFilterDTO filters){
        List<Product> products;

        if(filters.getCategoryId() == null){
            products = productRepository.findByCategory_Id(filters.getCategoryId());
        } else {
            products = productRepository.findAllWithVariants();
        }

        Stream<Product> productStream = products.stream();

        //Filter by product name
        if(filters.getSearchQuery() != null && !filters.getSearchQuery().isEmpty()){
            productStream = productStream.filter(product ->
                    product.getProductName().toLowerCase().contains(filters.getSearchQuery().toLowerCase())
            );
        }

        //Filter by price range
        if(filters.getMinPrice()!= null || filters.getMaxPrice()!= null){
            productStream = productStream.filter(product ->
                    product.getVariants().stream().anyMatch(variant -> {
                        boolean matchesMin = filters.getMinPrice() == null ||
                                variant.getPrice() >= filters.getMinPrice();
                        boolean matchesMax = filters.getMaxPrice() == null ||
                                variant.getPrice() <= filters.getMaxPrice();
                        return matchesMin && matchesMax;
                    })
            );
        }

        //Filter by color
        if(filters.getColor() != null && !filters.getColor().isEmpty()){
            productStream = productStream.filter(product ->
                    product.getVariants().stream().anyMatch(variant -> variant.getColor().equals(filters.getColor()))
            );
        }

        //Filter by storage
        if(filters.getStorage() != null && !filters.getStorage().isEmpty()){
            productStream = productStream.filter(product ->
                    product.getVariants().stream()
                            .anyMatch(variant -> variant.getStorage().equals(filters.getStorage()))
            );
        }

        //Sort the result
        if(filters.getSortBy() != null){
            Comparator<Product> comparator = null;
            switch (filters.getSortBy().toLowerCase()) {
                case "price":
                    comparator = Comparator.comparing(product ->
                            product.getVariants().stream()
                                    .mapToDouble(ProductVariant::getPrice)
                                    .min()
                                    .orElse(Double.MAX_VALUE)
                    );
                    break;

                case "name":
                    comparator = Comparator.comparing(Product::getProductName);
                    break;

                default:
                    comparator = Comparator.comparing(Product::getProductName);
            }
            if("desc".equals(filters.getSortOrder())){
                comparator = comparator.reversed();
            }

            productStream = productStream.sorted(comparator);
        }

        return productStream.collect(Collectors.toList());
    }

    public List<Product> searchProducts(String searchQuery){
        if(searchQuery == null || searchQuery.isEmpty()){
            return new ArrayList<>();
        }

        return productRepository.findByProductNameContainingIgnoreCase(searchQuery);
    }

    public Product getProductsWithVariants(Long id){
        return productRepository.findByIdWithVariants(id);
    }
}
