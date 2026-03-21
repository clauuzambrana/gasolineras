package com.claudia.gasolineras.service;

import com.claudia.gasolineras.entity.ActualizacionDatos;
import com.claudia.gasolineras.entity.Gasolinera;
import com.claudia.gasolineras.repository.ActualizacionDatosRepository;
import com.claudia.gasolineras.repository.GasolineraRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RutaService {
    private final GasolineraRepository gasolineraRepository;
    private final ActualizacionDatosRepository actualizacionRepository;
    private final RestTemplate restTemplate;

    @Value("${openrouteservice.apikey}")
    private String apiKey;

    private static final double MARGEN_GRADOS = 0.15;

    public RutaService(GasolineraRepository gasolineraRepository,
                       ActualizacionDatosRepository actualizacionRepository) {
        this.gasolineraRepository = gasolineraRepository;
        this.actualizacionRepository = actualizacionRepository;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> calcularRuta(String origen, String destino) {
        // 1. Geocodificar origen y destino
        double[] coordOrigen = geocodificar(origen);
        double[] coordDestino = geocodificar(destino);

        if (coordOrigen == null || coordDestino == null) {
            throw new RuntimeException("No se pudieron obtener las coordenadas de origen o destino");
        }

        // 2. Obtener ruta desde OpenRouteService
        List<double[]> puntosRuta = obtenerPuntosRuta(coordOrigen, coordDestino);

        // 3. Calcular bounding box con margen
        double latMin = puntosRuta.stream().mapToDouble(p -> p[1]).min().getAsDouble() - MARGEN_GRADOS;
        double latMax = puntosRuta.stream().mapToDouble(p -> p[1]).max().getAsDouble() + MARGEN_GRADOS;
        double lonMin = puntosRuta.stream().mapToDouble(p -> p[0]).min().getAsDouble() - MARGEN_GRADOS;
        double lonMax = puntosRuta.stream().mapToDouble(p -> p[0]).max().getAsDouble() + MARGEN_GRADOS;

        // 4. Filtrar gasolineras dentro del bounding box
        List<Gasolinera> gasolineras = gasolineraRepository
                .findByLatitudBetweenAndLongitudBetween(latMin, latMax, lonMin, lonMax);

        // 5. Encontrar la más barata
        Gasolinera masBarata = gasolineras.stream()
                .filter(g -> g.getPrecioGasolina95() != null)
                .min(Comparator.comparingDouble(Gasolinera::getPrecioGasolina95))
                .orElse(null);

        // 6. Obtener última actualización
        ActualizacionDatos ultimaActualizacion = actualizacionRepository
                .findTopByOrderByFechaDescargaDesc()
                .orElse(null);

        // 7. Construir respuesta
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("puntosRuta", puntosRuta);
        respuesta.put("gasolineras", gasolineras);
        respuesta.put("masBarata", masBarata);
        respuesta.put("ultimaActualizacion", ultimaActualizacion);
        respuesta.put("totalEncontradas", gasolineras.size());

        return respuesta;
    }

    private double[] geocodificar(String direccion) {
        String url = "https://api.openrouteservice.org/geocode/search?api_key=" + apiKey
                + "&text=" + direccion.replace(" ", "+") + "&size=1";

        Map<String, Object> respuesta = restTemplate.getForObject(url, Map.class);
        if (respuesta == null) return null;

        List<Map<String, Object>> features = (List<Map<String, Object>>) respuesta.get("features");
        if (features == null || features.isEmpty()) return null;

        Map<String, Object> geometry = (Map<String, Object>) features.get(0).get("geometry");
        List<Double> coordinates = (List<Double>) geometry.get("coordinates");

        return new double[]{coordinates.get(0), coordinates.get(1)};
    }

    private List<double[]> obtenerPuntosRuta(double[] origen, double[] destino) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

        Map<String, Object> body = new HashMap<>();
        body.put("coordinates", List.of(
                List.of(origen[0], origen[1]),
                List.of(destino[0], destino[1])
        ));

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> request =
                new org.springframework.http.HttpEntity<>(body, headers);

        Map<String, Object> respuesta = restTemplate.postForObject(url, request, Map.class);
        if (respuesta == null) return List.of();

        List<Map<String, Object>> features = (List<Map<String, Object>>) respuesta.get("features");
        if (features == null || features.isEmpty()) return List.of();

        Map<String, Object> geometry = (Map<String, Object>) features.get(0).get("geometry");
        List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

        List<double[]> puntos = new ArrayList<>();
        for (List<Double> coord : coordinates) {
            puntos.add(new double[]{coord.get(0), coord.get(1)});
        }
        return puntos;
    }
}