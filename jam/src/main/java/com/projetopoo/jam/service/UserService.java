package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.user.UserPasswordRequestDTO;
import com.projetopoo.jam.dto.user.UserResponseDTO;
import com.projetopoo.jam.dto.user.UserResquestDTO;
import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.ImageUtil;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        user.setUserPhoto(ImageUtil.createImage(userResquestDTO.getUserPhoto(), UPLOAD_DIRECTORY + "/photo", "/upload/user/photo/"));
        user.setUserBanner(ImageUtil.createImage(userResquestDTO.getUserBanner(), UPLOAD_DIRECTORY + "/banner", "/upload/user/banner/"));
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

            String newPhotoPath = ImageUtil.createImage(user.getUserPhoto(), UPLOAD_DIRECTORY + "/photo", "/upload/user/photo/");
            existingUser.setUserPhoto(newPhotoPath);

            ImageUtil.deleteImage(oldPhotoPath);
            user.setUserPhoto(null);
        }

        if (user.getUserBanner() != null && !user.getUserBanner().isEmpty()) {
            String oldBannerPath = existingUser.getUserBanner();

            String newBannerPath = ImageUtil.createImage(user.getUserBanner(), UPLOAD_DIRECTORY + "/banner", "/upload/user/banner/");
            existingUser.setUserBanner(newBannerPath);

            ImageUtil.deleteImage(oldBannerPath);
            user.setUserBanner(null);
        }

        modelMapper.map(user, existingUser);

        userRepository.save(existingUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUser(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public UserWithCurrentResponseDTO findUserId(Long userId, String identifier) {
        Optional<User> user = userRepository.findByUserId(userId);
        User currentUser = userRepository.findByIdentifier(identifier);
        if(user.isPresent()) {
            UserWithCurrentResponseDTO userWithCurrentResponseDTO = modelMapper.map(user.get(), UserWithCurrentResponseDTO.class);
            userWithCurrentResponseDTO.setUserCurrent(Objects.equals(userWithCurrentResponseDTO.getUserId(), currentUser.getUserId()));
            return userWithCurrentResponseDTO;
        } else {
            throw new EntityNotFoundException("Usuario não encontrado");
        }
    }


    @Transactional
    public void updatePassword(UserPasswordRequestDTO user, String identifier) throws IOException
    {
        User existingUser = userRepository.findByIdentifier(identifier);

        if (user.getUserNewPassword() != null)
        {
            if (!passwordEncoder.matches(user.getUserOldPassword(), existingUser.getUserPassword()))
            {
                throw new IllegalArgumentException("Senha incorreta");
            }
            existingUser.setUserPassword(passwordEncoder.encode(user.getUserNewPassword()));
        }
        else {
            throw new IllegalArgumentException("Senha vazia");
        }
        userRepository.save(existingUser);
    }
}
