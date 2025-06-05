package com.projetopoo.jam.service;

import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        if (userRepository.findByUserNameLogin(user.getUserNameLogin()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUserNameLogin());
        }

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        return userRepository.save(user);
    }

}
