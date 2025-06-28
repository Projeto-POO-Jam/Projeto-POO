package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface repository para classe Subscribe, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    /**
     * Função que busca uma inscrição com base em um subscribeUser e uma subscribeJam
     * @param subscribeUser Usuário que está sendo usado na consulta
     * @param subscribeJam Jam que está sendo usado na consulta
     * @return Inscrição com todas as informações sobre ela
     */
    Optional<Subscribe> findBySubscribeUserAndSubscribeJam(User subscribeUser, Jam subscribeJam);

    /**
     * Função que busca o total de inscrições em uma dada jam, com base no jamId
     * @param jamId Jam que está sendo usado na consulta
     * @return Quantidade de inscrições da jam
     */
    Long countBySubscribeJam_JamId(Long jamId);

    /**
     * Função que busca uma inscrição com base em um jogo
     * @param subscribeGame Jogo que está sendo usado na consulta
     * @return Inscrição com todas as informações sobre ela
     */
    Optional<Subscribe> findBySubscribeGame(Game subscribeGame);
}
