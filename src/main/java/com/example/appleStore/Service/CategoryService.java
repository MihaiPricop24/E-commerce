package com.example.appleStore.Service;

import com.example.appleStore.Model.Category;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll(){ return categoryRepository.findAll();}

    public Category getCategoryByID(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}
