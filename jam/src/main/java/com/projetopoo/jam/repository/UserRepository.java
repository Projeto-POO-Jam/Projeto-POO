package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserName(String userName);

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

    Optional<User> findByUserId(Long userId);
}
