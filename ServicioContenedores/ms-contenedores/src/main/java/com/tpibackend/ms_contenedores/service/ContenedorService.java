package com.tpibackend.ms_contenedores.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tpibackend.ms_contenedores.dto.ContenedorUbicacionDTO;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.repository.ContenedorRepository;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContenedorService {

    @Value("${ms.transportes.url}")
    private String apiUrlTransportes;

    private final ContenedorRepository contenedorRepository;

    private final RestTemplate restTemplate;

    public ContenedorService(
        ContenedorRepository contenedorRepository,
        RestTemplate restTemplate) {
        this.contenedorRepository = contenedorRepository;
        this.restTemplate = restTemplate;
    }

    public List<Contenedor> getContenedores() {
        return contenedorRepository.findAll();
    }

    public Contenedor getContenedorPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return contenedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenedor no encontrado con id: " + id));
    }

    public Contenedor persistirContenedor(Contenedor contenedor) {
        Objects.requireNonNull(contenedor, "El contenedor no puede ser nulo");
        return contenedorRepository.save(contenedor);
    }  // persistir contenedor sirve para el registro y para la modificacion, necesario para req 1.1.

    public void eliminarContenedorPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        if (!contenedorRepository.existsById(id)) {
            throw new EntityNotFoundException("Contenedor no encontrado con id: " + id);
        }
        contenedorRepository.deleteById(id);
    }

    public Estado getEstadoContenedor(Integer id) {
        Contenedor contenedor = getContenedorPorId(id);
        return contenedor.getEstado();
    }

    public List<Contenedor> getContenedoresPorEstado(Estado estado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        return contenedorRepository.findAll().stream()
                .filter(contenedor -> contenedor.getEstado().equals(estado))
                .toList();
    }

    public List<ContenedorUbicacionDTO> getContenedoresPendientes() {
    // Obtener contenedores pendientes
        List<Contenedor> listaContenedores = contenedorRepository.findAll().stream()
            .filter(contenedor -> contenedor.getEstado().getNombre().equals("pendiente"))
            .toList();

        // Crear lista de resultados
        List<ContenedorUbicacionDTO> resultado = new ArrayList<>();
        
        // Para cada contenedor, obtener su ubicación del servicio de transporte
        for (Contenedor contenedor : listaContenedores) {
            // Llamar al microservicio de transporte
            // TODO ver como carajo llamamos esto!!!!!!!
            String url = apiUrlTransportes + "/api/v1/depositos/"; // + contenedor.getIdDeposito() + "/ubicacion";
            
            // Obtener la ubicación (ajusta según la respuesta del servicio)
            // Obtener la respuesta como JSON (Map)
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> ubicacionJson = restTemplate.getForObject(url, java.util.Map.class);

            // Extraer latitud/longitud manejando números y strings
            double latitud = 0.0;
            double longitud = 0.0;
            if (ubicacionJson != null) {
                Object latObj = ubicacionJson.get("latitud");
                Object lonObj = ubicacionJson.get("longitud");

                if (latObj instanceof Number) {
                    latitud = ((Number) latObj).doubleValue();
                } else if (latObj != null) {
                    try { latitud = Double.parseDouble(latObj.toString()); } catch (NumberFormatException ignored) {}
                }

                if (lonObj instanceof Number) {
                    longitud = ((Number) lonObj).doubleValue();
                } else if (lonObj != null) {
                    try { longitud = Double.parseDouble(lonObj.toString()); } catch (NumberFormatException ignored) {}
                }
            }

            // Agregar al resultado
            ContenedorUbicacionDTO dto = new ContenedorUbicacionDTO(
                contenedor,
                latitud,
                longitud
            );
            resultado.add(dto);
        }
        
        return resultado;
    }
}
