package com.projetopoo.jam.service;

import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Classe para customizar o login, permitindo o uso do e-mail ou do nome para fazer o login
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;

        String name;
        String password;

        // Verifica se tem @ se tiver trata como e-mail se não trata como nome
        if(username.chars().filter(ch -> ch == '@').count() == 1){
            user = userRepository.findByUserEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
            name = user.getUserEmail();
            password = user.getUserPassword();
        } else {
            user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            name = user.getUserName();
            password = user.getUserPassword();
        }

        return new org.springframework.security.core.userdetails.User(
                name,
                password,
                new ArrayList<>()
        );
    }
}