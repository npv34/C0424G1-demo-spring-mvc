package com.codegym.demo.controllers;

import com.codegym.demo.dto.*;
import com.codegym.demo.dto.response.ListUserResponse;
import com.codegym.demo.dto.response.ListUserSearchResponse;
import com.codegym.demo.services.DepartmentService;
import com.codegym.demo.services.RoleService;
import com.codegym.demo.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    private final DepartmentService departmentService;
    private final HttpSession httpSession;
    private final RoleService roleService;

    public UserController(UserService userService,
                          DepartmentService departmentService,
                          HttpSession httpSession,
                          RoleService roleService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.httpSession = httpSession;
        this.roleService = roleService;
    }
    // This controller can handle user-related requests
    // Add methods to handle user operations like listing, creating, updating, and deleting users

    @GetMapping
    public String listUsers(@CookieValue(value = "counter", defaultValue = "1") String counter,
                            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                            Model model,
                            HttpServletResponse response) {
        if (page < 1) {
            page = 1; // Ensure page number is at least 1
        } else {
            page -= 1; // Convert to zero-based index for pagination
        }
        Cookie myCookie = new Cookie("msg", "Hello");

        int total = Integer.parseInt(counter) + 1;

        Cookie counterViewPage = new Cookie("counter", total + "");
        myCookie.setMaxAge(60);
        counterViewPage.setMaxAge(60);
        response.addCookie(myCookie);
        response.addCookie(counterViewPage);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        httpSession.setAttribute("email", email);

        ListUserResponse listUserResponse = userService.getAllUsers(page, size);
        List<UserDTO> users = listUserResponse.getUsers();
        // Logic to list users
        model.addAttribute("users", users);
        model.addAttribute("totalPages", listUserResponse.getTotalPage());
        model.addAttribute("totalViewPage", counter);
        return "users/list";
    }

    @GetMapping("/create")
    public String createUser(Model model) {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        List<RoleDTO> roles = roleService.getAllRoles();
        model.addAttribute("departments", departments);
        model.addAttribute("user", createUserDTO);
        model.addAttribute("roles", roles);
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
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

//    @ExceptionHandler(RuntimeException.class)
//    public String handlerRuntimeException(){
//        return "errors/500";
//    }

    @PostMapping("/create")
    public String storeUser(@Validated @ModelAttribute("user") CreateUserDTO
                                        createUserDTO, BindingResult result, Model model ) throws IOException {
        if (result.hasErrors()){
            List<DepartmentDTO> departments = departmentService.getAllDepartments();
            List<RoleDTO> roles = roleService.getAllRoles();
            model.addAttribute("departments", departments);
            model.addAttribute("roles", roles);
            return "users/create";
        }
        // Logic to store a new user
        userService.storeUser(createUserDTO);
        return "redirect:/admin/users";
    }
//
    @GetMapping("/{id}/edit")
    public String showFormEdit(@PathVariable("id") int id, Model model) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return "errors/404"; // Redirect if user not found
        }

        // Prepare the EditUserDTO with the user's current details
        EditUserDTO editUserDTO = new EditUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
        editUserDTO.setDepartmentId(user.getDepartmentId());

        List<DepartmentDTO> departments = departmentService.getAllDepartments();

        // Add the EditUserDTO to the model
        model.addAttribute("user", editUserDTO);
        model.addAttribute("departments", departments);

        return "users/edit"; // This will resolve to /WEB-INF/views/users/edit.html
    }
//
    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") int id,
                             @ModelAttribute("user") EditUserDTO editUserDTO) throws IOException {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return "errors/404";
        }
        userService.updateUser(id, editUserDTO);
        return "redirect:/admin/users";
    }

    @GetMapping("/search")
    public ResponseEntity<ListUserSearchResponse> search(@RequestParam("keyword") String keyword) {
        ListUserSearchResponse res = userService.searchByName(keyword);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
