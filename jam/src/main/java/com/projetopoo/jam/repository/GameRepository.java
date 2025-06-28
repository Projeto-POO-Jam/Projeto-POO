package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface repository para classe Game, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    /**
     * Função que busca um jogo pelo gameId
     * @param gameId Id do jogo que está sendo usado na consulta
     * @return Jogo com todas as informações sobre ele
     */
    Optional<Game> findByGameId(long gameId);

    /**
     * Função que busca uma lista paginada de jogos, com base em um jamId
     * @param jamId Id da jam que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Lista paginada contendo diversos jogos que correspondem a busca ordenados com base no total de votos.
     */
    @Query("SELECT g " +
            "FROM " +
                "Game g " +
            "WHERE " +
                "g.gameSubscribe.subscribeJam.jamId = :jamId " +
            "ORDER BY " +
                "SIZE(g.gameVotes) DESC")
    Page<Game> findByJamIdOrderByVotes(@Param("jamId") Long jamId, Pageable pageable);

    /**
     * Função que busca uma lista paginada de jogos, com base em um userId
     * @param userId Id da usuário que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Lista paginada contendo diversos jogos que correspondem a busca ordenados com base no total de votos.
     */
    @Query("SELECT g " +
            "FROM " +
                "Game g " +
            "WHERE " +
                "g.gameSubscribe.subscribeUser.userId = :userId " +
            "ORDER BY " +
                "SIZE(g.gameVotes) DESC")
    Page<Game> findByUserIdOrderByVotes(@Param("userId") Long userId, Pageable pageable);

    /**
     * Função que busca uma lista paginada de jogos, com todos os jogos do banco de dados
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Lista paginada contendo diversos jogos ordenados com base no total de votos.
     */
    @Query("SELECT g " +
            "FROM " +
                "Game g " +
            "ORDER BY " +
                "SIZE(g.gameVotes) DESC")
    Page<Game> findAllByOrderByVotes(Pageable pageable);

    /**
     * Função que busca uma lista paginada de jogos, com todos os jogos em que um dado usuário votou, com base no userId
     * @param userId Id da usuário que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Lista paginada contendo diversos jogos que correspondem a busca ordenados do que foi mais votado para o que foi menos votado.
     */
    @Query("SELECT g " +
            "FROM " +
                "Game g, " +
                "Vote v " +
            "WHERE " +
                "g.gameId = v.voteGame.gameId " +
                "AND v.voteUser.userId = :userId " +
            "ORDER BY " +
                "v.voteId DESC")
    Page<Game> findByUserIdOrderByGameId(@Param("userId") Long userId, Pageable pageable);

}