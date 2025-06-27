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

@Repository
public interface JamRepository extends JpaRepository<Jam, Long> {
    Optional<Jam> findByJamId(long jamId);

    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "YEAR(j.jamStartDate) = :year " +
                "AND MONTH(j.jamStartDate) = :month")
    Page<Jam> findByYearAndMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);

    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "j.jamStatus IN :statuses " +
            "ORDER BY " +
                "SIZE(j.jamSubscribes) DESC")
    Page<Jam> findTopJamsByJamStatus(@Param("statuses") List<JamStatus> statuses, Pageable pageable);

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

    //  "SIZE(j.jamSubscribes) DESC")

    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "j.jamUser.userId = :userId " +
            "ORDER BY " +
                "SIZE(j.jamSubscribes) DESC")
    Page<Jam> findByUserIdOrderByJamSubscribes(@Param("userId") Long userId, Pageable pageable);

}
