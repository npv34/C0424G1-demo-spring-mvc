package com.codegym.demo.controllers;

import com.codegym.demo.entities.User;
import com.codegym.demo.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    // This controller can handle user-related requests
    // Add methods to handle user operations like listing, creating, updating, and deleting users

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        // Logic to list users
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/create")
    public String createUser() {
        // Logic to create a new user
        return "users/create"; // This will resolve to /WEB-INF/views/users/create.html
    }

    @GetMapping("/{id}/detail")
    public String userDetail(@PathVariable("id") String id,
                             Model model) {
        model.addAttribute("id", id);
        return "users/detail"; // This will resolve to /WEB-INF/views/users/detail.html
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") int id) {
        // Logic to delete a user by ID
        userService.deleteById(id);
        // For now, just redirect to the list of users
        return "redirect:/users";
    }
}
