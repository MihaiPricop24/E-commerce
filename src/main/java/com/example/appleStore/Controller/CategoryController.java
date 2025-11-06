package com.example.appleStore.Controller;

import com.example.appleStore.Model.Category;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Service.CategoryService;
import com.example.appleStore.Service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // --- Get category by ID ---
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // --- Create new category ---
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    // --- Update category ---
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return categoryService.updateCategory(id, updatedCategory);
    }

    // --- Delete category ---
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    // --- Get all products under one category ---
    @GetMapping("/{id}/products")
    public List<Product> getProductsByCategory(@PathVariable Long id) {
        return productService.getProductsByCategoryId(id);
    }
}
