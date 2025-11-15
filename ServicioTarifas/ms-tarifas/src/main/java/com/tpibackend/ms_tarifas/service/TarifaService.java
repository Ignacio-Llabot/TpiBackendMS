package com.tpibackend.ms_tarifas.service;

import java.util.Objects;
import org.springframework.stereotype.Service;
import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.repository.TarifaRepository;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;


@Service
public class TarifaService {
    
    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }


    public Tarifa getTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        return tarifaRepository.findById(id).orElse(null);
    }

    public List<Tarifa> getTarifas() {
        Objects.requireNonNull(tarifaRepository, "El repositorio de tarifas no puede ser nulo");
        return tarifaRepository.findAll();
    }

    public Tarifa persistirTarifa(Tarifa tarifa) {
        Objects.requireNonNull(tarifa, "La tarifa no puede ser nula");
        return tarifaRepository.save(tarifa);
    }  // persistir tarifa sirve para el registro y para la modificacion

    public void eliminarTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        if (!tarifaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa no encontrada con id: " + id);
        }
        tarifaRepository.deleteById(id);
    }

}
