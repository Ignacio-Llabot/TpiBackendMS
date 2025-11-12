package org.tpibackend.mstransportes.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.TipoTramo;
import org.tpibackend.mstransportes.repository.TipoTramoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TipoTramoService {

    private final TipoTramoRepository tipoTramoRepository;

    public TipoTramoService(TipoTramoRepository tipoTramoRepository) {
        this.tipoTramoRepository = tipoTramoRepository;
    }

    public TipoTramo getTipoTramoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return tipoTramoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoTramo no encontrado con id: " + id));
    }

    public TipoTramo getTipoTramoPorNombre(String nombre) {
        Objects.requireNonNull(nombre, "el nombre no puede ser nulo");
        return tipoTramoRepository.findByNombre(nombre)
                .orElseThrow(() -> new EntityNotFoundException("TipoTramo no encontrado con nombre: " + nombre));
    }
    
}
