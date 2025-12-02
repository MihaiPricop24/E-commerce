package com.example.appleStore.Controller;

import com.example.appleStore.Model.Cart;
import com.example.appleStore.Model.ProductVariant;
import com.example.appleStore.Model.User;
import com.example.appleStore.Service.CartService;
import com.example.appleStore.Service.ProductVariantService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductVariantService productVariantService;

    @GetMapping
    public String viewCart(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            List<Cart> cartItems = cartService.getCartByUser(user.getId());

            Double total = cartItems.stream()
                    .mapToDouble(item -> item.getProductVariant().getPrice() * item.getQuantity())
                    .sum();

            Integer itemCount = cartItems.stream()
                    .mapToInt(Cart::getQuantity)
                    .sum();

            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("itemCount", itemCount);

            return "cart";
        } catch (Exception e) {
            model.addAttribute("message", "Error loading cart: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long variantId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession httpSession,
                            RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            ProductVariant variant = productVariantService.findById(variantId);

            if (variant == null) {
                redirectAttributes.addFlashAttribute("error", "Product variant not found");
                return "redirect:/";
            }

            if (variant.getQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("error", "Not enough stock available");
                return "redirect:/product/" + variant.getProduct().getId();
            }

            cartService.addtoCart(user.getId(), variantId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart!");

            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding to cart: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/update/{itemId}")
    public String updateCartItem(@PathVariable Long itemId,
                                 @RequestParam Integer quantity,
                                 HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (quantity <= 0) {
                cartService.removeFromCart(itemId);
                redirectAttributes.addFlashAttribute("success", "Item removed from cart");
            } else {
                cartService.updateCartItem(itemId, quantity);
                redirectAttributes.addFlashAttribute("success", "Cart updated");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId,
                                 HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.removeFromCart(itemId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing item: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.clearCart(user.getId());
            redirectAttributes.addFlashAttribute("success", "Cart cleared");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error clearing cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }
}