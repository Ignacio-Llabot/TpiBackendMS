package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Estado;
import org.tpibackend.mstransportes.repository.EstadoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public Estado getEstadoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return estadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado con id: " + id));
    }
    

}
