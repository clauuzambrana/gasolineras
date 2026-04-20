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

    public RutaService(GasolineraRepository gasolineraRepository,
                       ActualizacionDatosRepository actualizacionRepository) {
        this.gasolineraRepository = gasolineraRepository;
        this.actualizacionRepository = actualizacionRepository;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> calcularRuta(String origen, String destino, String marca) {
    double[] coordOrigen = geocodificar(origen);
    double[] coordDestino = geocodificar(destino);

    if (coordOrigen == null || coordDestino == null) {
        throw new RuntimeException("No se pudieron obtener las coordenadas de origen o destino");
    }

    List<double[]> puntosRuta = obtenerPuntosRuta(coordOrigen, coordDestino);

    // Bounding box ampliado solo para prefiltrar eficientemente
    double latMin = puntosRuta.stream().mapToDouble(p -> p[1]).min().getAsDouble() - 0.1;
    double latMax = puntosRuta.stream().mapToDouble(p -> p[1]).max().getAsDouble() + 0.1;
    double lonMin = puntosRuta.stream().mapToDouble(p -> p[0]).min().getAsDouble() - 0.1;
    double lonMax = puntosRuta.stream().mapToDouble(p -> p[0]).max().getAsDouble() + 0.1;

    List<Gasolinera> candidatas = gasolineraRepository
            .findByLatitudBetweenAndLongitudBetween(latMin, latMax, lonMin, lonMax);

    // Filtrar por distancia real a los puntos de la ruta
    double radioKm = 2;
    List<Gasolinera> gasolineras = candidatas.stream()
        .filter(g -> estasCercaDeRuta(g.getLatitud(), g.getLongitud(), puntosRuta, radioKm))
        .filter(g -> marca == null || marca.isEmpty() ||
        g.getMarca() != null && g.getMarca().toLowerCase().contains(marca.toLowerCase()))
        .collect(java.util.stream.Collectors.toList());

    Gasolinera masBarata = gasolineras.stream()
            .filter(g -> g.getPrecioGasolina95() != null)
            .min(Comparator.comparingDouble(Gasolinera::getPrecioGasolina95))
            .orElse(null);

    ActualizacionDatos ultimaActualizacion = actualizacionRepository
            .findTopByOrderByFechaDescargaDesc()
            .orElse(null);
    
    // Calular precio min y max para luego hacer media y mostrar por colores de precios las gasolineras
    double precioMin = gasolineras.stream()
        .filter(g -> g.getPrecioGasolina95() != null)
        .mapToDouble(Gasolinera::getPrecioGasolina95)
        .min().orElse(0);

    double precioMax = gasolineras.stream()
            .filter(g -> g.getPrecioGasolina95() != null)
            .mapToDouble(Gasolinera::getPrecioGasolina95)
            .max().orElse(0);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("puntosRuta", puntosRuta);
    respuesta.put("gasolineras", gasolineras);
    respuesta.put("masBarata", masBarata);
    respuesta.put("ultimaActualizacion", ultimaActualizacion);
    respuesta.put("totalEncontradas", gasolineras.size());
    respuesta.put("precioMin", precioMin);
    respuesta.put("precioMax", precioMax);

    return respuesta;
}

private boolean estasCercaDeRuta(double lat, double lon, List<double[]> puntosRuta, double radioKm) {
    for (double[] punto : puntosRuta) {
        double pLon = punto[0];
        double pLat = punto[1];
        if (distanciaKm(lat, lon, pLat, pLon) <= radioKm) {
            return true;
        }
    }
    return false;
}

private double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
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