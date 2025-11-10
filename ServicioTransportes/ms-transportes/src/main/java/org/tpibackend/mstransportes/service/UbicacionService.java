package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.repository.UbicacionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UbicacionService {
    
    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    public Ubicacion getUbicacionPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ubicacion no encontrada con id: " + id));
    }

    public Ubicacion persistirUbicacion(Ubicacion ubicacion) {
        Objects.requireNonNull(ubicacion, "la ubicacion no puede ser nula");
        return ubicacionRepository.save(ubicacion);
    }

}
