package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface repository para classe Jam, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface JamRepository extends JpaRepository<Jam, Long> {
    /**
     * Função que busca uma jam pelo jamId
     * @param jamId Id do jogo que está sendo usado na consulta
     * @return Se existir alguma correspondência, retorna uma jam com todas as informações sobre ela
     */
    Optional<Jam> findByJamId(long jamId);

    /**
     * Função que busca uma lista paginada de jams, com base em um ano e mês
     * @param year Ano que está sendo usada na consulta
     * @param month Mês que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Uma página contendo diversas jams que correspondem a busca.
     */
    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "YEAR(j.jamStartDate) = :year " +
                "AND MONTH(j.jamStartDate) = :month")
    Page<Jam> findByYearAndMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);

    /**
     * Função que busca uma lista paginada de jams, com base no status
     * @param statuses Lista com os possíveis status que serão buscados
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Uma página contendo diversas jams que correspondem a busca em ordem do número de inscritos.
     */
    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "j.jamStatus IN :statuses " +
            "ORDER BY " +
                "SIZE(j.jamSubscribes) DESC")
    Page<Jam> findTopJamsByJamStatus(@Param("statuses") List<JamStatus> statuses, Pageable pageable);

    /**
     * Função que busca uma lista paginada de jams, com todas as jams em que um dado usuário se inscreveu, com base no userId
     * @param userId Id do usuário que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Uma página contendo diversas jams que correspondem a busca em ordem do jamId.
     */
    @Query("SELECT j " +
            "FROM " +
                "Jam j, " +
                "Subscribe s " +
            "WHERE " +
                "j.jamId = s.subscribeJam.jamId " +
                "AND s.subscribeUser.userId = :userId " +
            "ORDER BY " +
                "j.jamId DESC")
    Page<Jam> findByUserIdOrderByJamId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Função que busca uma lista paginada de jams, com todas as jams criadas por um dado usuário, com base no userId
     * @param userId Ano que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Uma página contendo diversas jams que correspondem a busca em ordem do número de inscrições.
     */
    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "j.jamUser.userId = :userId " +
            "ORDER BY " +
                "SIZE(j.jamSubscribes) DESC")
    Page<Jam> findByUserIdOrderByJamSubscribes(@Param("userId") Long userId, Pageable pageable);

}
