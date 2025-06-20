package com.projetopoo.jam.repository;

import com.projetopoo.jam.dto.UserResponseDTO;
import com.projetopoo.jam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findBySubscribeUserAndSubscribeJam(User subscribeUser, Jam subscribeJam);
    long countBySubscribeJam_JamId(Long jamId);

    UserResponseDTO findBySubscribeUser(User subscribeUser);

    User findBySubscribeGame(Game subscribeGame);
}
