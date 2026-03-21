package com.claudia.gasolineras.controller;

import com.claudia.gasolineras.service.RutaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RutaController {
    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping("/ruta")
    public ResponseEntity<Map<String, Object>> calcularRuta(
            @RequestParam String origen,
            @RequestParam String destino) {
        Map<String, Object> resultado = rutaService.calcularRuta(origen, destino);
        return ResponseEntity.ok(resultado);
    }
}