package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.CommentResponseDTO;
import com.projetopoo.jam.dto.UserResponseDTO;
import com.projetopoo.jam.dto.UserResquestDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.ImageUtil;
import com.projetopoo.jam.util.UpdateUtil;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ModelMapper modelMapper;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/user";

    @Transactional
    public void createUser(UserResquestDTO userResquestDTO) throws IOException {
        List<String> validationErrors = new ArrayList<>();

        User user = modelMapper.map(userResquestDTO, User.class);

        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            validationErrors.add("USERNAME_EXISTS");
        }

        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            validationErrors.add("EMAIL_EXISTS");
        }

        if (!validationErrors.isEmpty()) {
            throw new UserValidationException(validationErrors);
        }

        user.setUserPhoto(ImageUtil.createImage(userResquestDTO.getUserPhoto(), UPLOAD_DIRECTORY, "/upload/user/"));
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserResquestDTO user, String identifier) throws IOException {
        List<String> validationErrors = new ArrayList<>();

        User existingUser = userRepository.findByIdentifier(identifier);

        if (user.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        if (user.getUserName() != null && !user.getUserName().equals(existingUser.getUserName())) {
            if (userRepository.findByUserName(user.getUserName()).isPresent()) {
                validationErrors.add("USERNAME_EXISTS");
            }
        }

        if (user.getUserEmail() != null && !user.getUserEmail().equals(existingUser.getUserEmail())) {
            if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
                validationErrors.add("EMAIL_EXISTS");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new UserValidationException(validationErrors);
        }


        if (user.getUserPhoto() != null && !user.getUserPhoto().isEmpty()) {
            String oldPhotoPath = existingUser.getUserPhoto();

            String newPhotoPath = ImageUtil.createImage(user.getUserPhoto(), UPLOAD_DIRECTORY, "/upload/user/");
            existingUser.setUserPhoto(newPhotoPath);

            ImageUtil.deleteImage(oldPhotoPath);
            user.setUserPhoto(null);
        }

        modelMapper.map(user, existingUser);

        userRepository.save(existingUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUser(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        return modelMapper.map(user, UserResponseDTO.class);
    }

}
