package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface repository para classe User, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Função que busca um usuário pelo userEmail
     * @param userEmail E-mail do usuário que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna um usuário com todas as informações sobre ele
     */
    Optional<User> findByUserEmail(String userEmail);

    /**
     * Função que busca um usuário pelo userName
     * @param userName Nome do usuário que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna um usuário com todas as informações sobre ele
     */
    Optional<User> findByUserName(String userName);

    /**
     * Função que busca um usuário pelo identificador
     * @param identifier Pode ser tanto o nome quanto o e-mail do usuário que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna um usuário com todas as informações sobre ele
     */
    default User findByIdentifier(String identifier) {
        User user;

        if (identifier.contains("@")) {
            Optional<User> optionalUser = findByUserEmail(identifier);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                throw new EntityNotFoundException("User not found with email: " + identifier);
            }
        } else {
            Optional<User> optionalUser = findByUserName(identifier);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                throw new EntityNotFoundException("User not found with username: " + identifier);
            }
        }

        return user;
    }

    /**
     * Função que busca um usuário pelo userId
     * @param userId Id do usuário que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna um usuário com todas as informações sobre ele
     */
    Optional<User> findByUserId(Long userId);
}
