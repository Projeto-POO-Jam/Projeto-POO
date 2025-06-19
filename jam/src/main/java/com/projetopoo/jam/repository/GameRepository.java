package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameId(long gameId);
    Boolean existsGameByGameSubscribe(Subscribe gameSubscribe);
}