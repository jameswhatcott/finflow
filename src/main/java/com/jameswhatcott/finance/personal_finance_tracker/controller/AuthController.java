package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.UserRole;
import com.jameswhatcott.finance.personal_finance_tracker.service.UserService;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "username", required = false) String username,
                           Model model) {
        // Add page title
        model.addAttribute("title", "Login");
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
            if (username != null && !username.isEmpty()) {
                model.addAttribute("username", username);
            }
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully.");
        }
        return "pages/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        // Add page title
        model.addAttribute("title", "Register");
        
        return "pages/register";
    }
    
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Check if user already exists
            if (userService.getUserByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/register";
            }
            
            if (userService.getUserByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/register";
            }
            
            // Create new user
            User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(UserRole.USER)
                .build();
            
            userService.createUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
