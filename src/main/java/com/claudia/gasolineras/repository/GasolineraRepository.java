package com.claudia.gasolineras.repository;

import com.claudia.gasolineras.entity.Gasolinera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GasolineraRepository extends JpaRepository<Gasolinera, Long> {
    List<Gasolinera> findByLatitudBetweenAndLongitudBetween(
        Double latMin, Double latMax,
        Double lonMin, Double lonMax
    );
    void deleteAll();
}