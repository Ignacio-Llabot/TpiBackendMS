package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.TipoCamion;
import org.tpibackend.mstransportes.repository.TipoCamionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TipoCamionService {

    private final TipoCamionRepository tipoCamionRepository;
    private static final Logger log = LoggerFactory.getLogger(TipoCamionService.class);

    public TipoCamionService(TipoCamionRepository tipoCamionRepository) {
        this.tipoCamionRepository = tipoCamionRepository;
    }

    public TipoCamion getTipoCamionPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return tipoCamionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo de camión {} no encontrado", id);
                    return new EntityNotFoundException("TipoCamion no encontrado con id: " + id);
                });
    }
    
    
    
}
