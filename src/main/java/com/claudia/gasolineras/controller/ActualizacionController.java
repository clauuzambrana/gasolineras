package com.claudia.gasolineras.controller;

import com.claudia.gasolineras.entity.ActualizacionDatos;
import com.claudia.gasolineras.service.ActualizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ActualizacionController {
    private final ActualizacionService actualizacionService;

    public ActualizacionController(ActualizacionService actualizacionService) {
        this.actualizacionService = actualizacionService;
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
}