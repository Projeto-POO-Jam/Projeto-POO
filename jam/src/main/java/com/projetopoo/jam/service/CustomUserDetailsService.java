package com.projetopoo.jam.service;

import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;

        String name;
        String password;

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