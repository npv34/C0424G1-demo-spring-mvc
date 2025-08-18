package com.codegym.demo.services;

import com.codegym.demo.dto.CreateUserDTO;
import com.codegym.demo.dto.EditUserDTO;
import com.codegym.demo.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private List<User> users;
    private static final String UPLOAD_DIR = "/Users/luanpv/Desktop/demo/src/main/webapp/WEB-INF/resources/uploads";

    public UserService(){
        // Initialize with some dummy data
        this.users = new ArrayList<>();
        this.users.add( new User(1,"u1", "john_doe", "u1@gamil.com", "00898989"));
        this.users.add( new User(2,"u2", "john_doe", "u2@gamil.com", "00898989"));
        this.users.add( new User(3,"u3", "john_doe", "u3@gamil.com", "00898989"));

    }

    public List<User> getAllUsers() {
        return users;
    }

    public void deleteById(int id){
        // Logic to delete a user by ID
        users.removeIf(user -> user.getId() == id);
    }

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


        User lastUser = users.get(users.size() - 1);
        int newId = lastUser.getId() + 1; // Increment ID based on the last user

        // Create a new User object with the provided details
        User newUser = new User(username, password, email, phone);
        newUser.setId(newId); // Set the new ID
        newUser.setImageUrl("/resources/uploads/" + fileName); // Set the image URL

        users.add(newUser); // Add the new user to the list
    }

    public User getUserById(int id) {
        // Logic to get a user by ID
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null; // Return null if user not found
    }

    public void updateUser(int id, EditUserDTO editUserDTO) {
        User user = getUserById(id);
        if (user != null) {
            // Update user details
            user.setUsername(editUserDTO.getUsername());
            user.setEmail(editUserDTO.getEmail());
            user.setPhone(editUserDTO.getPhone());
            // Note: Password is not updated in this method
        }
    }
}
