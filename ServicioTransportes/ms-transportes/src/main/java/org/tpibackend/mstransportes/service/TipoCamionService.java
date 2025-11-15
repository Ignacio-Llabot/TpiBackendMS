package org.tpibackend.mstransportes.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.TipoCamion;
import org.tpibackend.mstransportes.repository.TipoCamionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TipoCamionService {

    private final TipoCamionRepository tipoCamionRepository;

    public TipoCamionService(TipoCamionRepository tipoCamionRepository) {
        this.tipoCamionRepository = tipoCamionRepository;
    }

    public TipoCamion getTipoCamionPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return tipoCamionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoCamion no encontrado con id: " + id));
    }
    
    
    
}
