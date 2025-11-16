package com.tpibackend.ms_contenedores.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.repository.EstadoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepository;
    private static final Logger log = LoggerFactory.getLogger(EstadoService.class);

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<Estado> getEstados() {
        log.info("Recuperando estados");
        return estadoRepository.findAll();
    }

    public Estado getEstadoPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado {} no encontrado", id);
                    return new EntityNotFoundException("Estado no encontradoc con id: " + id);
                });
    }

    public Estado getEstadoPorNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        return estadoRepository.findAll().stream()
                .filter(estado -> estado.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Estado {} no encontrado", nombre);
                    return new EntityNotFoundException("Estado no encontrado con nombre: " + nombre);
                });
    }

    public Estado persistirEstado(Estado estado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        Estado guardado = estadoRepository.save(estado);
        log.info("Estado persistido con id {}", guardado.getIdEstado());
        return guardado;
    }  // persistir estado sirve para el registro y para la modificacion. Necesario par req 1.3

    public void eliminarEstadoPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        if (!estadoRepository.existsById(id)) {
            throw new EntityNotFoundException("Estado no encontrado con id: " + id);
        }
        log.info("Eliminando estado {}", id);
        estadoRepository.deleteById(id);
    }
}