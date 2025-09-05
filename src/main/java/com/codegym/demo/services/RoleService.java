package com.codegym.demo.services;

import com.codegym.demo.dto.RoleDTO;
import com.codegym.demo.models.Role;
import com.codegym.demo.repositories.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    private IRoleRepository roleRepository;

    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles() {
        List<Role> data = roleRepository.findAll();
        List<RoleDTO> roleDTOS = new ArrayList<>();
        for (Role item: data) {
            RoleDTO roleDTO = new RoleDTO(item.getId(), item.getName());
            roleDTOS.add(roleDTO);
        }

        return roleDTOS;
    }
}
