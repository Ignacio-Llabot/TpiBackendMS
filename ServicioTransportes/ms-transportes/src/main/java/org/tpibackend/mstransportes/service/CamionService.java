package org.tpibackend.mstransportes.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Camion;
import org.tpibackend.mstransportes.repository.CamionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CamionService {

    private final CamionRepository camionRepository;

    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }
    
    public List<Camion> getCamiones() {
        return camionRepository.findAll();
    }

    public Camion getCamionPorPatente(String patente) {
        Objects.requireNonNull(patente, "la patente no puede ser nula");
        return camionRepository.findById(patente)
            .orElseThrow(() -> new EntityNotFoundException("Camión no encontrado con patente: " + patente));
    }

    public Camion persistirCamion(Camion camion) {
        Objects.requireNonNull(camion, "el camión no puede ser nulo");
        return camionRepository.save(camion);
    }  // persistir camión sirve para el registro y para la modificacion

    public void eliminarCamionPorPatente(String patente) {
        Objects.requireNonNull(patente, "la patente no puede ser nula");
        if (!camionRepository.existsById(patente)) {
            throw new EntityNotFoundException("Camión no encontrado con patente: " + patente);
        }
        camionRepository.deleteById(patente);
    }


}
