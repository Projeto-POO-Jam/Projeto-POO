package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JamRepository extends JpaRepository<Jam, Long> {
    Optional<Jam> findByJamId(long jamId);
}
