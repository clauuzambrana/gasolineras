package com.claudia.gasolineras.repository;

import com.claudia.gasolineras.entity.Gasolinera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GasolineraRepository extends JpaRepository<Gasolinera, Long> {
    List<Gasolinera> findByLatitudBetweenAndLongitudBetween(
        Double latMin, Double latMax,
        Double lonMin, Double lonMax
    );
    void deleteAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Gasolinera g")
    void borrarTodas();
}