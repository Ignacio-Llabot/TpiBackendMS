package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Transportista;
import org.tpibackend.mstransportes.repository.TransportistaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransportistaService {
    
    private final TransportistaRepository transportistaRepository;

    public TransportistaService(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public Transportista getTransportistaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return transportistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con id: " + id));
    }
    
}
