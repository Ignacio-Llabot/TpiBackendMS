package org.tpibackend.mstransportes.service;

import java.util.List;
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

    public Tramo getTramoPorId(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        return tramoRepository.findById(tramoId)
                .orElseThrow(() -> new EntityNotFoundException("Tramo no encontrado con id: " + tramoId));
    }

    public Tramo persistirTramo(Tramo tramo) {
        Objects.requireNonNull(tramo, "el tramo no puede ser nulo");
        return tramoRepository.save(tramo);
    }

    public void eliminarTramoById(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        if (!tramoRepository.existsById(tramoId)) {
            throw new EntityNotFoundException("Tramo no encontrado con id: " + tramoId);
        }
        tramoRepository.deleteById(tramoId);
    }

    public List<Tramo> guardarTramos(List<Tramo> tramos) {
        Objects.requireNonNull(tramos, "la lista de tramos no puede ser nula");
        return tramoRepository.saveAll(tramos);
    }
}
