package com.example.appleStore.Controller;

import com.example.appleStore.Model.Category;
import com.example.appleStore.Model.Product;
import com.example.appleStore.Model.User;
import com.example.appleStore.Service.CategoryService;
import com.example.appleStore.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String homepage(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }

        try {
            List<Product> products = productService.getAllProducts();

            List<Category> categories = categoryService.getAll();

            model.addAttribute("user", user);
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategory", "All Products");

            return "home";
        } catch(Exception e){
            model.addAttribute("message", "Error loading products: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/category/{categoryId}")
    public String productsByCategory(@PathVariable Long categoryId, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }

        try{

            List<Product> products = productService.getProductByCategory(categoryId);

            List<Category> categories = categoryService.getAll();

            Category selectedCategory = categoryService.getCategoryByID(categoryId);

            model.addAttribute("user", user);
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategory", selectedCategory);

            return "home";
        } catch(Exception e){
            model.addAttribute("message", "Error loading products: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }

        try{

            Product product = productService.getProductsWithVariants(id);

            if(product == null){
                model.addAttribute("message", "Product not found");
                return "error";
            }

            model.addAttribute("user", user);
            model.addAttribute("product", product);

            return "product-detail";
        } catch(Exception e){
            model.addAttribute("message", "Error loading products: " + e.getMessage());
            return "error";
        }
    }
}
