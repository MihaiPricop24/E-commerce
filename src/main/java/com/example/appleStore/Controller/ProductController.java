package com.example.appleStore.Controller;

import com.example.appleStore.DTO.ProductFilterDTO;
import com.example.appleStore.Model.Category;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Model.User;
import com.example.appleStore.Service.CategoryService;
import com.example.appleStore.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String homepage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            HttpSession httpSession,
            Model model) {

        User user = getAuthenticatedUser(httpSession);
        if(user == null) return "redirect:/login";

        try {
            ProductFilterDTO filters = buildFilters(search, null, minPrice, maxPrice, sortBy);
            List<Product> products = productService.filterProducts(filters);

            setupModel(model, user, products, "All Products");
            return "home";
        } catch(Exception e){
            return handleError(model, e);
        }
    }

    // Helper methods
    private User getAuthenticatedUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private ProductFilterDTO buildFilters(String search, Long categoryId,
                                          Double minPrice, Double maxPrice, String sortBy) {
        ProductFilterDTO filters = new ProductFilterDTO();
        filters.setSearchQuery(search);
        filters.setCategoryId(categoryId);
        filters.setMinPrice(minPrice);
        filters.setMaxPrice(maxPrice);
        filters.setSortBy(sortBy);
        filters.setSortOrder("asc");
        return filters;
    }

    private void setupModel(Model model, User user, List<Product> products, String selectedCategory) {
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("selectedCategory", selectedCategory);
    }

    private String handleError(Model model, Exception e) {
        model.addAttribute("message", "Error loading products: " + e.getMessage());
        return "error";
    }
}