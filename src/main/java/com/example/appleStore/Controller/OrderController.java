package com.example.appleStore.Controller;

import com.example.appleStore.Model.Order;
import com.example.appleStore.Model.User;
import com.example.appleStore.Model.Cart;
import com.example.appleStore.Service.CartService;
import com.example.appleStore.Service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping
    public String viewOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }

        try{
            List<Order> orders = orderService.getOrdersByUser(user.getId());
            model.addAttribute("user", user);
            model.addAttribute("orders", orders);
            return "orders";
        } catch (Exception e){
            model.addAttribute("message", "Error loading order: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                model.addAttribute("message", "Order not found");
                return "error";
            }

            model.addAttribute("user", user);
            model.addAttribute("order", order);
            return "order-details";
        } catch (Exception e) {
            model.addAttribute("message", "Error loading order: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try{
            List<Cart> cartItems = cartService.getCartByUser(user.getId());

            if(cartItems.isEmpty()){
                return "redirect:/cart";
            }

            Double total = cartItems.stream()
                    .mapToDouble(item -> item.getProductVariant().getPrice() * item.getQuantity())
                    .sum();

            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            return "checkout";
        } catch (Exception e) {
            model.addAttribute("message", "Error loading checkout: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam String shippingAddress,
                             @RequestParam String shippingCity,
                             @RequestParam String phoneNumber,
                             HttpSession httpSession,
                             RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.createOrder(user.getId());
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error placing order: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,
                              HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId);
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

}
