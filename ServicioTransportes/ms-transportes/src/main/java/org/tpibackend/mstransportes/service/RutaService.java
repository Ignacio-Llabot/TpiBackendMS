package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.repository.RutaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RutaService {
    
    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public Ruta getRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));
    }

    public Ruta persistirRuta(Ruta ruta) {
        Objects.requireNonNull(ruta, "la ruta no puede ser nula");
        return rutaRepository.save(ruta);
    }

    public void eliminarRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        if (!rutaRepository.existsById(id)) {
        throw new EntityNotFoundException("Ruta no encontrada con id: " + id);
        }
        rutaRepository.deleteById(id);
    }


}
