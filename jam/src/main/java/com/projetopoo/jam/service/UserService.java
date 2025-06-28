package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.user.UserPasswordRequestDTO;
import com.projetopoo.jam.dto.user.UserResponseDTO;
import com.projetopoo.jam.dto.user.UserRequestDTO;
import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.FileUtil;

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

/**
 * Classe service para classe User, responsável pela lógica de negócios
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/user";

    /**
     * Constrói uma nova instância de UserService com suas dependências
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param passwordEncoder Classe criptografar a senha
     * @param modelMapper Classe para mapear transformações entre models e DTOs
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Função para criar um novo usuário
     * @param userRequestDTO Informações sobre o usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem
     */
    @Transactional
    public void createUser(UserRequestDTO userRequestDTO) throws IOException {
        // Cria lista de erros
        List<String> validationErrors = new ArrayList<>();

        // Passa as informações recebidas para o formato de User
        User user = modelMapper.map(userRequestDTO, User.class);

        // Verifica se o nome do usuário já está sendo usado
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            // Adiciona erro a lista de erros
            validationErrors.add("USERNAME_EXISTS");
        }

        // Verifica se o e-mail do usuário já está sendo usado
        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            // Adiciona erro a lista de erros
            validationErrors.add("EMAIL_EXISTS");
        }

        // Verifica se a lista de erros está vazia
        if (!validationErrors.isEmpty()) {
            throw new UserValidationException(validationErrors);
        }

        // Salva imagens
        user.setUserPhoto(FileUtil.createFile(userRequestDTO.getUserPhoto(), UPLOAD_DIRECTORY + "/photo", "/upload/user/photo/"));
        user.setUserBanner(FileUtil.createFile(userRequestDTO.getUserBanner(), UPLOAD_DIRECTORY + "/banner", "/upload/user/banner/"));

        // Criptografa a senha se ela não for nula
        if (user.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        // Salva o novo usuário
        userRepository.save(user);
    }

    /**
     * Função para atualizar as informações sobre um usuário
     * @param user Novas informações sobre o usuário
     * @param identifier Identificador do usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem
     */
    @Transactional
    public void updateUser(UserRequestDTO user, String identifier) throws IOException {
        // Cria lista de erros
        List<String> validationErrors = new ArrayList<>();

        // Busca usuário que fez a requisição
        User existingUser = userRepository.findByIdentifier(identifier);

        // Verifica se o nome do usuário já está sendo usado
        if (user.getUserName() != null && !user.getUserName().equals(existingUser.getUserName())) {
            // Adiciona erro a lista de erros
            if (userRepository.findByUserName(user.getUserName()).isPresent()) {
                validationErrors.add("USERNAME_EXISTS");
            }
        }

        // Verifica se o e-mail do usuário já está sendo usado
        if (user.getUserEmail() != null && !user.getUserEmail().equals(existingUser.getUserEmail())) {
            // Adiciona erro a lista de erros
            if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
                validationErrors.add("EMAIL_EXISTS");
            }
        }

        // Verifica se a lista de erros está vazia
        if (!validationErrors.isEmpty()) {
            throw new UserValidationException(validationErrors);
        }

        //Verifica se as imagens mudaram e atualiza elas caso necessário
        if (user.getUserPhoto() != null && !user.getUserPhoto().isEmpty()) {
            String oldPhotoPath = existingUser.getUserPhoto();

            String newPhotoPath = FileUtil.createFile(user.getUserPhoto(), UPLOAD_DIRECTORY + "/photo", "/upload/user/photo/");
            existingUser.setUserPhoto(newPhotoPath);

            FileUtil.deleteFile(oldPhotoPath);
            user.setUserPhoto(null);
        }

        if (user.getUserBanner() != null && !user.getUserBanner().isEmpty()) {
            String oldBannerPath = existingUser.getUserBanner();

            String newBannerPath = FileUtil.createFile(user.getUserBanner(), UPLOAD_DIRECTORY + "/banner", "/upload/user/banner/");
            existingUser.setUserBanner(newBannerPath);

            FileUtil.deleteFile(oldBannerPath);
            user.setUserBanner(null);
        }

        // Passa os novos dados do usuário para o usuário existente, isso evita salvar dados com null no lugar de informações não alteradas
        modelMapper.map(user, existingUser);

        // Salva o usuário com os dados novos
        userRepository.save(existingUser);
    }


    /**
     * Função para buscar usuário logado
     * @param identifier Identificador do usuário
     * @return Informações do usuário logado
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findUser(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    /**
     * Função para buscar usuário com pelo Id
     * @param userId Id do usuário que está sendo usado na consulta
     * @param identifier Identificador do usuário
     * @return Informações do usuário
     */
    @Transactional(readOnly = true)
    public UserWithCurrentResponseDTO findUserId(Long userId, String identifier) {
        // Busca usuário que fez a requisição
        User currentUser = userRepository.findByIdentifier(identifier);

        // Verifica se o usuário existe
        Optional<User> user = userRepository.findByUserId(userId);
        if(user.isPresent()) {
            // Passa o usuário para o formato de resposta
            UserWithCurrentResponseDTO userWithCurrentResponseDTO = modelMapper.map(user.get(), UserWithCurrentResponseDTO.class);

            // Verifica se o usuário logado é o usuário consultado
            userWithCurrentResponseDTO.setUserCurrent(Objects.equals(userWithCurrentResponseDTO.getUserId(), currentUser.getUserId()));
            return userWithCurrentResponseDTO;
        } else {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
    }


    @Transactional
    public void updatePassword(UserPasswordRequestDTO user, String identifier) throws IOException {
        User existingUser = userRepository.findByIdentifier(identifier);

        if (user.getUserNewPassword() != null) {
            if (!passwordEncoder.matches(user.getUserOldPassword(), existingUser.getUserPassword())) {
                throw new IllegalArgumentException("Senha incorreta");
            }
            existingUser.setUserPassword(passwordEncoder.encode(user.getUserNewPassword()));
        } else {
            throw new IllegalArgumentException("Senha vazia");
        }
        userRepository.save(existingUser);
    }
}
