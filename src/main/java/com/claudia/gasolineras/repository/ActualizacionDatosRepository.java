package com.claudia.gasolineras.repository;

import com.claudia.gasolineras.entity.ActualizacionDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ActualizacionDatosRepository extends JpaRepository<ActualizacionDatos, Long> {
    Optional<ActualizacionDatos> findTopByOrderByFechaDescargaDesc();
}