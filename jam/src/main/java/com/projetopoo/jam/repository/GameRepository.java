package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameId(long gameId);

    @Query("SELECT g " +
            "FROM " +
                "Game g " +
            "WHERE " +
                "g.gameSubscribe.subscribeJam.jamId = :jamId " +
            "ORDER BY " +
                "SIZE(g.gameVotes) DESC")
    Page<Game> findByJamIdOrderByVotes(@Param("jamId") Long jamId, Pageable pageable);

    @Query("SELECT g " +
            "FROM " +
                "Game g " +
            "WHERE " +
                "g.gameSubscribe.subscribeUser.userId = :userId " +
            "ORDER BY " +
                "SIZE(g.gameVotes) DESC")
    Page<Game> findByUserIdOrderByVotes(@Param("userId") Long userId, Pageable pageable);

}