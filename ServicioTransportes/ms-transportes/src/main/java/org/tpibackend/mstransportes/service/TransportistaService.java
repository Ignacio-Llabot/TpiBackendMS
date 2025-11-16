package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Transportista;
import org.tpibackend.mstransportes.repository.TransportistaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransportistaService {
    
    private final TransportistaRepository transportistaRepository;
    private static final Logger log = LoggerFactory.getLogger(TransportistaService.class);

    public TransportistaService(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public Transportista getTransportistaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return transportistaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transportista {} no encontrado", id);
                    return new EntityNotFoundException("Transportista no encontrado con id: " + id);
                });
    }
    
}
