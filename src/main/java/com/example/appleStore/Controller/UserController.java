package com.example.appleStore.Controller;

import com.example.appleStore.Model.User;
import com.example.appleStore.Repository.UserRepository;
import com.example.appleStore.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {  // Spring injects automatically
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/"; // user already logged in
        }
        return "register"; // loads register.html (Thymeleaf template)
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/"; // user already logged in
        }
        return "login";
    }

    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute("user"));
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("phoneNumber") String phoneNumber,
                               @RequestParam("address") String address,
                               @RequestParam("city") String city,
                               Model model) {
        try {
            User newUser = new User();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setPassword(password); // raw password for now — will be hashed in service
            newUser.setPhoneNumber(phoneNumber);
            newUser.setAddress(address);
            newUser.setCity(city);

            userService.registerUser(newUser);

            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("message", "❌ Registration failed: " + e.getMessage());
        }

        return "register"; // reloads page and shows message
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        // ✅ store user info in session
        session.setAttribute("user", user);

        return "redirect:/";
    }
}
