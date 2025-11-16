package org.tpibackend.mstransportes.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.repository.DepositoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepositoService {
    
    private final DepositoRepository depositoRepository;
    private static final Logger log = LoggerFactory.getLogger(DepositoService.class);

    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public List<Deposito> getDepositos() {
        log.info("Recuperando depósitos");
        return depositoRepository.findAll();
    }

    public Deposito getDepositoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return depositoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Depósito {} no encontrado", id);
                return new EntityNotFoundException("Deposito no encontrado con id: " + id);
            });
    }

    public Deposito persistirDeposito(Deposito deposito) {
        Objects.requireNonNull(deposito, "el deposito no puede ser nulo");
        Deposito guardado = depositoRepository.save(deposito);
        log.info("Depósito {} persistido", guardado.getIdDeposito());
        return guardado;
    }   // persiste cambios o camión nuevo si no existe

    public void eliminarDepositoPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        if (!depositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Deposito no encontrado con id" + id);
        }
        log.info("Eliminando depósito {}", id);
        depositoRepository.deleteById(id);
    }
}
