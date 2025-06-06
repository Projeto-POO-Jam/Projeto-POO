package com.projetopoo.jam.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.UpdateUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(User user) {
        if (userRepository.findByUserNameLogin(user.getUserNameLogin()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUserNameLogin());
        }

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        if (user.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        Optional<User> optionalUser = userRepository.findById(user.getUserId());
        User existingUser;

        if (optionalUser.isPresent()) {
            existingUser = optionalUser.get();
        } else {
            throw new EntityNotFoundException("User not found with id: " + user.getUserId());
        }

        UpdateUtils.copyNonNullProperties(user, existingUser,
                "userNameLogin", "userVotes", "userComments");



        userRepository.save(existingUser);
    }

}
