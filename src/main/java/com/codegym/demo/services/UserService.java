package com.codegym.demo.services;

import com.codegym.demo.dto.CreateUserDTO;
import com.codegym.demo.dto.EditUserDTO;
import com.codegym.demo.dto.RoleDTO;
import com.codegym.demo.dto.UserDTO;
import com.codegym.demo.dto.response.ListUserResponse;
import com.codegym.demo.dto.response.ListUserSearchResponse;
import com.codegym.demo.mappers.CreateUserMapper;
import com.codegym.demo.models.Department;
import com.codegym.demo.models.Role;
import com.codegym.demo.models.User;
import com.codegym.demo.repositories.IDepartmentRepository;
import com.codegym.demo.repositories.IRoleRepository;
import com.codegym.demo.repositories.IUserRepository;
import com.codegym.demo.untils.FileManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    private static final String UPLOAD_DIR = "/Users/luanpv/Desktop/demo/uploads";
    private final IUserRepository userRepository;
    private final IDepartmentRepository departmentRepository;
    private final FileManager fileManager;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreateUserMapper createUserMapper;

    public UserService(IUserRepository userRepository,
                       IDepartmentRepository departmentRepository,
                       FileManager fileManager, IRoleRepository roleRepository, PasswordEncoder passwordEncoder, CreateUserMapper createUserMapper){
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.fileManager = fileManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.createUserMapper = createUserMapper;
    }

    public ListUserResponse getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        Page<User> data = userRepository.findAll(pageable);
        List<User> users = data.getContent();

        // map data Entity to DTO
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId().intValue());
            userDTO.setUsername(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setImageUrl(user.getImageUrl());

            String nameDepartment = user.getDepartment() != null ? user.getDepartment().getName() : "No Department";
            userDTO.setDepartmentName(nameDepartment);

            List<String> nameRoles = new ArrayList<>();
            // get roleName
            for (Role role: user.getRoles()) {
                nameRoles.add(role.getName());
            }

            userDTO.setNameRoles(nameRoles);
            userDTOs.add(userDTO);
        }

        ListUserResponse listUserResponse = new ListUserResponse();
        listUserResponse.setTotalPage(data.getTotalPages());
        listUserResponse.setCurrentPage(data.getNumber());
        listUserResponse.setUsers(userDTOs);

        return listUserResponse;
    }

    public void deleteById(int id){
        // Logic to delete a user by ID
        Optional<User> user = userRepository.findById((long)(id));
        if (user.isPresent()) {
            User currentUser = user.get();
            // delete image
            fileManager.deleteFile(UPLOAD_DIR + "/" + currentUser.getImageUrl());
            userRepository.delete(currentUser);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @Transactional
    public void storeUser(CreateUserDTO dto) {
        // 1) Upload ảnh (nếu có)
        String imageUrl = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            imageUrl = fileManager.uploadFile(UPLOAD_DIR, dto.getImage());
        }

        // 2) Tìm Department (nếu có)
        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
        }

        // 3. Roles
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));

        // 4) Mã hoá mật khẩu
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User newUser = createUserMapper.toEntity(dto, encodedPassword, department, roles, imageUrl);
        userRepository.save(newUser);
    }

    public UserDTO getUserById(int id) {
        Optional<User> user = userRepository.findById((long) id);
        if (user.isPresent()) {
            User currentUser = user.get();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(currentUser.getId().intValue());
            userDTO.setUsername(currentUser.getName());
            userDTO.setEmail(currentUser.getEmail());
            userDTO.setPhone(currentUser.getPhone());
            userDTO.setImageUrl(currentUser.getImageUrl());
            userDTO.setDepartmentId(currentUser.getDepartment() != null ? currentUser.getDepartment().getId() : null);
            return userDTO;
        }
        return null;
    }
//
    public void updateUser(int id, EditUserDTO editUserDTO) {
        Optional<User> user = userRepository.findById((long)id);
        if (user.isPresent()) {
            // Update user details
            User currentUser = user.get();
            currentUser.setName(editUserDTO.getUsername());
            currentUser.setEmail(editUserDTO.getEmail());
            currentUser.setPhone(editUserDTO.getPhone());

            Long departmentId = editUserDTO.getDepartmentId();
            if (departmentId != null) {
                Department department = departmentRepository.findById(departmentId).orElse(null);
                if (department != null) {
                    currentUser.setDepartment(department);
                }
            }
            MultipartFile file = editUserDTO.getImage();
            if (!file.isEmpty()) {
                // 1. xoa anh cu
                fileManager.deleteFile(UPLOAD_DIR + "/" + currentUser.getImageUrl());
                String fileName = fileManager.uploadFile(UPLOAD_DIR, file);
                currentUser.setImageUrl(fileName); // Set the image URL
            }

            userRepository.save(currentUser);
        }
    }

    public ListUserSearchResponse searchByName(String name) {
        List<User> data = userRepository.findUserByNameContaining(name);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : data) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId().intValue());
            userDTO.setUsername(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setImageUrl(user.getImageUrl());

            String nameDepartment = user.getDepartment() != null ? user.getDepartment().getName() : "No Department";
            userDTO.setDepartmentName(nameDepartment);

            userDTOList.add(userDTO);
        }

        ListUserSearchResponse listUserSearchResponse = new ListUserSearchResponse();
        listUserSearchResponse.setUsers(userDTOList);

        return listUserSearchResponse;
    }
}
