package org.tpibackend.mstransportes.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.repository.DepositoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepositoService {
    
    private final DepositoRepository depositoRepository;

    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public List<Deposito> getDepositos() {
        return depositoRepository.findAll();
    }

    public Deposito getDepositoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return depositoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Deposito no encontrado con id: " + id));
    }

    public Deposito persistirDeposito(Deposito deposito) {
        Objects.requireNonNull(deposito, "el deposito no puede ser nulo");
        return depositoRepository.save(deposito);
    }   // persiste cambios o camión nuevo si no existe

    public void eliminarDepositoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        if (!depositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Deposito no encontrado con id" + id);
        }
        depositoRepository.deleteById(id);
    }
}
