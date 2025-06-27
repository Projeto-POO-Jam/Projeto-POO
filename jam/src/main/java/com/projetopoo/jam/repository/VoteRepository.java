package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoteUserAndVoteGame(User voteUser, Game voteGame);
    long countByVoteGame_GameId(Long gameId);
}
