package com.projetopoo.jam.service;

import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.ImageUtil;
import com.projetopoo.jam.util.UpdateUtils;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/user";

    @Transactional
    public void createUser(User user, MultipartFile photo) throws IOException {
        List<String> validationErrors = new ArrayList<>();

        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            validationErrors.add("USERNAME_EXISTS");
        }

        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            validationErrors.add("EMAIL_EXISTS");
        }

        if (!validationErrors.isEmpty()) {
            throw new UserValidationException(validationErrors);
        }

        user.setUserPhoto(ImageUtil.createImage(photo, UPLOAD_DIRECTORY, "/upload/user/"));
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user, MultipartFile photo) throws IOException {
        Optional<User> optionalUser = userRepository.findById(user.getUserId());
        User existingUser;

        if (user.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        if (optionalUser.isPresent()) {
            existingUser = optionalUser.get();
        } else {
            throw new EntityNotFoundException("User not found with id: " + user.getUserId());
        }

        if (photo != null && !photo.isEmpty()) {
            String oldPhotoPath = existingUser.getUserPhoto();

            String newPhotoPath = ImageUtil.createImage(photo, UPLOAD_DIRECTORY, "/upload/user/");
            existingUser.setUserPhoto(newPhotoPath);

            ImageUtil.deleteImage(oldPhotoPath);
        }

        UpdateUtils.copyNonNullProperties(user, existingUser,
                "userName", "userPhoto", "userVotes", "userComments");

        userRepository.save(existingUser);
    }


    @Transactional(readOnly = true)
    public User findUser(String identifier) {
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByUserEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identifier));
        } else {
            user = userRepository.findByUserName(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + identifier));
        }

        user.setUserPassword(null);
        return user;
    }

}
