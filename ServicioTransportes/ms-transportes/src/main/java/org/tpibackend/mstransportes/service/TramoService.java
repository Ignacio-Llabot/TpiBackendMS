package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.repository.TramoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TramoService {
    
    private final TramoRepository tramoRepository;

    public TramoService(TramoRepository tramoRepository) {
        this.tramoRepository = tramoRepository;
    }

    public Tramo getTramoPorId(int origenId, int destinoId) {
        Objects.requireNonNull(origenId, "la id origen no puede ser nula");
        Objects.requireNonNull(destinoId, "la id destino no puede ser nula");
        return tramoRepository.findById(new org.tpibackend.mstransportes.entity.TramoId(origenId, destinoId))
                .orElseThrow(() -> new EntityNotFoundException("Tramo no encontrado con origenId: " + origenId + " y destinoId: " + destinoId));
    }

    public Tramo persistirTramo(Tramo tramo) {
        Objects.requireNonNull(tramo, "el tramo no puede ser nulo");
        return tramoRepository.save(tramo);
    }

    public void eliminarTramoById(int origenId, int destinoId) {
        Objects.requireNonNull(origenId, "la id origen no puede ser nula");
        Objects.requireNonNull(destinoId, "la id destino no puede ser nula");
        org.tpibackend.mstransportes.entity.TramoId tramoId = new org.tpibackend.mstransportes.entity.TramoId(origenId, destinoId);
        if (!tramoRepository.existsById(tramoId)) {
            throw new EntityNotFoundException("Tramo no encontrado con origenId: " + origenId + " y destinoId: " + destinoId);
        }
        tramoRepository.deleteById(tramoId);
    }
}
