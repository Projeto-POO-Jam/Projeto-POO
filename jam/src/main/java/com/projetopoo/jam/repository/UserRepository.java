package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserId(int userId);
}
