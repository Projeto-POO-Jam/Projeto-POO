package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface repository para classe Vote, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    /**
     * Função que busca uma inscrição com base em um voteUser e um voteGame
     * @param voteUser Usuário que está sendo usado na consulta
     * @param voteGame Jogo que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna um voto com todas as informações sobre ele
     */
    Optional<Vote> findByVoteUserAndVoteGame(User voteUser, Game voteGame);

    /**
     * Função que busca o total de votos em um dado jogo, com base no gameId
     * @param gameId Jam que está sendo usado na consulta
     * @return A quantidade de inscrições da jam
     */
    long countByVoteGame_GameId(Long gameId);
}
