package com.claudia.gasolineras.service;

import com.claudia.gasolineras.entity.ActualizacionDatos;
import com.claudia.gasolineras.entity.Gasolinera;
import com.claudia.gasolineras.repository.ActualizacionDatosRepository;
import com.claudia.gasolineras.repository.GasolineraRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ActualizacionService {
    private final ActualizacionDatosRepository actualizacionRepository;
    private final GasolineraRepository gasolineraRepository;
    private final RestTemplate restTemplate;

    private static final String URL_GOBIERNO =
        "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/";

    public ActualizacionService(
            ActualizacionDatosRepository actualizacionRepository,
            GasolineraRepository gasolineraRepository) {
        this.actualizacionRepository = actualizacionRepository;
        this.gasolineraRepository = gasolineraRepository;
        this.restTemplate = new RestTemplate();
    }

    public String actualizarDatos() {
        // 1. Llamar a la API del gobierno
        Map<String, Object> respuesta = restTemplate.getForObject(URL_GOBIERNO, Map.class);

        if (respuesta == null || !respuesta.containsKey("ListaEESSPrecio")) {
            return "Error: no se pudo obtener datos del gobierno";
        }

        List<Map<String, Object>> listaEstaciones =
            (List<Map<String, Object>>) respuesta.get("ListaEESSPrecio");

        // 2. Crear registro de actualización
        ActualizacionDatos actualizacion = new ActualizacionDatos();
        actualizacion.setFechaDescarga(LocalDateTime.now());
        actualizacion.setFuente(URL_GOBIERNO);
        actualizacion.setNumeroRegistros(listaEstaciones.size());
        actualizacion = actualizacionRepository.save(actualizacion);

        // 3. Borrar gasolineras anteriores y cargar las nuevas
        gasolineraRepository.deleteAll();

        List<Gasolinera> nuevasGasolineras = new ArrayList<>();

        for (Map<String, Object> estacion : listaEstaciones) {
            try {
                Gasolinera g = new Gasolinera();

                g.setNombre(getString(estacion, "Rótulo"));
                g.setMarca(getString(estacion, "Rótulo"));
                g.setDireccion(getString(estacion, "Dirección"));
                g.setMunicipio(getString(estacion, "Municipio"));
                g.setProvincia(getString(estacion, "Provincia"));
                g.setLatitud(parseDouble(getString(estacion, "Latitud")));
                g.setLongitud(parseDouble(getString(estacion, "Longitud (WGS84)")));
                g.setPrecioGasolina95(parseDouble(getString(estacion, "Precio Gasolina 95 E5")));
                g.setPrecioDiesel(parseDouble(getString(estacion, "Precio Gasoil A")));
                g.setActualizacion(actualizacion);

                // Solo añadir si tiene coordenadas y precio válidos
                if (g.getLatitud() != null && g.getLongitud() != null
                        && g.getPrecioGasolina95() != null) {
                    nuevasGasolineras.add(g);
                }

            } catch (Exception e) {
                // Si una gasolinera tiene datos malformados, la saltamos
            }
        }

        gasolineraRepository.saveAll(nuevasGasolineras);

        return "Actualización completada. Gasolineras cargadas: " + nuevasGasolineras.size();
    }

    public ActualizacionDatos obtenerUltimaActualizacion() {
        return actualizacionRepository.findTopByOrderByFechaDescargaDesc().orElse(null);
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString().trim() : null;
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            // La API del gobierno usa coma como separador decimal
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}