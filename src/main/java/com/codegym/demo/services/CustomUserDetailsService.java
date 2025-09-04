package com.codegym.demo.services;

import com.codegym.demo.models.User;
import com.codegym.demo.repositories.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository  = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User userLogin = user.get();

        return new org.springframework.security.core.userdetails.User(
                userLogin.getEmail(),
                userLogin.getPassword(),
                userLogin.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }
}
