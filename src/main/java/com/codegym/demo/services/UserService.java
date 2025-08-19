package com.codegym.demo.services;

import com.codegym.demo.dao.UserDAO;
import com.codegym.demo.dto.CreateUserDTO;
import com.codegym.demo.dto.EditUserDTO;
import com.codegym.demo.dto.UserDTO;
import com.codegym.demo.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private static final String UPLOAD_DIR = "/Users/luanpv/Desktop/demo/src/main/webapp/WEB-INF/resources/uploads";
    private UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userDAO.findAll();
        // map data Entity to DTO
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId().intValue());
            userDTO.setUsername(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setImageUrl(user.getImageUrl());
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }
//
    public void deleteById(int id){
        // Logic to delete a user by ID
        User user = userDAO.findById((long) id);
        if (user != null) {
            userDAO.delete(user);
        }
    }
//
    public void storeUser(CreateUserDTO createUserDTO) throws IOException {
        // Logic to store a new user
        String username = createUserDTO.getUsername();
        String password = createUserDTO.getPassword();
        String email = createUserDTO.getEmail();
        String phone = createUserDTO.getPhone();

        MultipartFile file = createUserDTO.getImage();

        String fileName = file.getOriginalFilename();

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File dest = new File(UPLOAD_DIR + "/" + fileName);
        file.transferTo(dest);

        // Create a new User object with the provided details
        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setPhone(phone);

        newUser.setImageUrl("/resources/uploads/" + fileName); // Set the image URL

        userDAO.save(newUser);
    }
//
    public UserDTO getUserById(int id) {
        User user = userDAO.findById((long) id);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId().intValue());
            userDTO.setUsername(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setImageUrl(user.getImageUrl());
            return userDTO;
        }
        return null;
    }
//
    public void updateUser(int id, EditUserDTO editUserDTO) {
        User user = userDAO.findById((long)id);
        if (user != null) {
            // Update user details
            user.setName(editUserDTO.getUsername());
            user.setEmail(editUserDTO.getEmail());
            user.setPhone(editUserDTO.getPhone());
            userDAO.update(user);
        }
    }
}
