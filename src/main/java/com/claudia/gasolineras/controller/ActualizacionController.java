package com.claudia.gasolineras.controller;

import com.claudia.gasolineras.entity.ActualizacionDatos;
import com.claudia.gasolineras.service.ActualizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ActualizacionController {
    private final ActualizacionService actualizacionService;
    private final com.claudia.gasolineras.repository.GasolineraRepository gasolineraRepository;

    public ActualizacionController(ActualizacionService actualizacionService,
            com.claudia.gasolineras.repository.GasolineraRepository gasolineraRepository) {
        this.actualizacionService = actualizacionService;
        this.gasolineraRepository = gasolineraRepository;
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> actualizarDatos() {
        String resultado = actualizacionService.actualizarDatos();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ultima-actualizacion")
    public ResponseEntity<ActualizacionDatos> ultimaActualizacion() {
        ActualizacionDatos ultima = actualizacionService.obtenerUltimaActualizacion();
        if (ultima == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ultima);
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<String>> obtenerMarcas() {
        List<String> marcas = gasolineraRepository.findMarcasDistintas();
        return ResponseEntity.ok(marcas);
    }
}