package com.codegym.demo.services;

import com.codegym.demo.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private List<User> users;

    public UserService(){
        // Initialize with some dummy data
        this.users = new ArrayList<>();
        this.users.add( new User(1,"u1", "john_doe", "password123", "00898989"));
        this.users.add( new User(2,"u2", "john_doe", "password123", "00898989"));
        this.users.add( new User(3,"u3", "john_doe", "password123", "00898989"));

    }

    public List<User> getAllUsers() {
        return users;
    }

    public void deleteById(int id){
        // Logic to delete a user by ID
        users.removeIf(user -> user.getId() == id);
    }
}
