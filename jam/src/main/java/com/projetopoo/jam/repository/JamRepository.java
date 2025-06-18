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
public interface JamRepository extends JpaRepository<Jam, Long> {
    Optional<Jam> findByJamId(long jamId);

    @Query("SELECT j " +
            "FROM " +
                "Jam j " +
            "WHERE " +
                "YEAR(j.jamStartDate) = :year " +
                "AND MONTH(j.jamStartDate) = :month")
    Page<Jam> findByYearAndMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);
}
