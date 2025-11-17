package com.tpibackend.ms_contenedores.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tpibackend.ms_contenedores.dto.ContenedorUbicacionDTO;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.repository.ContenedorRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContenedorService {

    @Value("${ms.transportes.url}")
    private String apiUrlTransportes;

    private final ContenedorRepository contenedorRepository;

    private final RestTemplate restTemplate;

    private final EstadoService estadoService;

    private static final Logger log = LoggerFactory.getLogger(ContenedorService.class);

    public ContenedorService(
        ContenedorRepository contenedorRepository,
        RestTemplate restTemplate,
        EstadoService estadoService) {
        this.contenedorRepository = contenedorRepository;
        this.restTemplate = restTemplate;
        this.estadoService = estadoService;
    }

    public List<Contenedor> getContenedores() {
        log.info("Recuperando contenedores");
        return contenedorRepository.findAll();
    }

    public Contenedor getContenedorPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return contenedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Contenedor {} no encontrado", id);
                    return new EntityNotFoundException("Contenedor no encontrado con id: " + id);
                });
    }

    public Contenedor persistirContenedor(Contenedor contenedor) {
        Objects.requireNonNull(contenedor, "El contenedor no puede ser nulo");
        Contenedor guardado = contenedorRepository.save(contenedor);
        log.info("Contenedor {} persistido", guardado.getIdContenedor());
        return guardado;
    }  // persistir contenedor sirve para el registro y para la modificacion, necesario para req 1.1.

    public void eliminarContenedorPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        if (!contenedorRepository.existsById(id)) {
            throw new EntityNotFoundException("Contenedor no encontrado con id: " + id);
        }
        log.info("Eliminando contenedor {}", id);
        contenedorRepository.deleteById(id);
    }

    public Estado getEstadoContenedor(Integer id) {
        Contenedor contenedor = getContenedorPorId(id);
        return contenedor.getEstado();
    }

    public List<Contenedor> getContenedoresPorEstado(Estado estado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        log.info("Buscando contenedores por estado {}", estado.getNombre());
        return contenedorRepository.findAll().stream()
                .filter(contenedor -> contenedor.getEstado().equals(estado))
                .toList();
    }

    public void actualizarEstado(Integer contenedorId, String nombreEstado) {
        Objects.requireNonNull(contenedorId, "El id del contenedor no puede ser nulo");
        Objects.requireNonNull(nombreEstado, "El nombre del estado no puede ser nulo");

        log.info("Actualizando estado del contenedor {} a {}", contenedorId, nombreEstado);

        Contenedor contenedor = getContenedorPorId(contenedorId);
        Estado nuevoEstado = estadoService.getEstadoPorNombre(nombreEstado);
        contenedor.setEstado(nuevoEstado);

        contenedorRepository.save(contenedor);
        log.info("Estado del contenedor {} actualizado a {}", contenedorId, nombreEstado);
    }

    public List<ContenedorUbicacionDTO> getContenedoresPendientes() {
        log.info("Iniciando búsqueda de contenedores pendientes");
    // Obtener contenedores pendientes
        List<Contenedor> listaContenedores = contenedorRepository.findAll().stream()
            .filter(contenedor -> contenedor.getEstado().getNombre().equals("por retirar"))
            .toList();
        log.info("Se encontraron {} contenedores pendientes", listaContenedores.size());

        // Crear lista de resultados
        List<ContenedorUbicacionDTO> resultado = new ArrayList<>();

        // Para cada contenedor, obtener su ubicación del servicio de transporte
        for (Contenedor contenedor : listaContenedores) {
            // Llamar al microservicio de transporte
            // TODO ver como carajo llamamos esto!!!!!!!
            String url = apiUrlTransportes + "/api/v1/depositos/"; // + contenedor.getIdDeposito() + "/ubicacion";

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> ubicacionJson = restTemplate.getForObject(url, Map.class);

                // Extraer latitud/longitud manejando números y strings
                double latitud = 0.0;
                double longitud = 0.0;
                if (ubicacionJson != null) {
                    Object latObj = ubicacionJson.get("latitud");
                    Object lonObj = ubicacionJson.get("longitud");

                    latitud = parseCoordinate(latObj);
                    longitud = parseCoordinate(lonObj);
                }

                ContenedorUbicacionDTO dto = new ContenedorUbicacionDTO(
                    contenedor,
                    latitud,
                    longitud
                );
                resultado.add(dto);
            } catch (RestClientException ex) {
                log.error("No se pudo obtener la ubicación del contenedor {}: {}", contenedor.getIdContenedor(), ex.getMessage());
            }
        }

        log.info("Se construyeron {} registros de tracking", resultado.size());
        return resultado;
    }

    private double parseCoordinate(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null && StringUtils.hasText(value.toString())) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ex) {
                log.warn("Valor de coordenada inválido: {}", value);
            }
        }
        return 0.0;
    }
}
