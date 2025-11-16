package org.tpibackend.mstransportes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.DepositoService;
import org.tpibackend.mstransportes.service.UbicacionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/depositos")

public class DepositoController {

    private final DepositoService depositoService;
    private final UbicacionService ubicacionService;
    private static final Logger log = LoggerFactory.getLogger(DepositoController.class);

    public DepositoController(DepositoService depositoService, UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
        this.depositoService = depositoService;
    }

    // Definimos endpoints

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> getDepositoPorId(@PathVariable Integer id) {
        log.info("Recuperando depósito {}", id);
        try {
            Deposito deposito = depositoService.getDepositoPorId(id);
            log.info("Depósito {} recuperado", id);
            return ResponseEntity.ok(deposito);
        } catch (EntityNotFoundException ex) {
            log.warn("Depósito {} no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> putDeposito(@PathVariable Integer id, @RequestBody Deposito deposito) {
        log.info("Actualizando depósito {}", id);
        try {
            Deposito miDeposito = depositoService.getDepositoPorId(id);

            miDeposito.setCostoEstadia(deposito.getCostoEstadia());

            if (deposito.getUbicacion() != null && deposito.getUbicacion().getIdUbicacion() != null) {
                Ubicacion nuevaUbicacion = ubicacionService.getUbicacionPorId(deposito.getUbicacion().getIdUbicacion());
                miDeposito.setUbicacion(nuevaUbicacion);
            }

            Deposito actualizado = depositoService.persistirDeposito(miDeposito);
            log.info("Depósito {} actualizado", id);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException ex) {
            log.warn("No se pudo actualizar el depósito {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping
    public ResponseEntity<Deposito> postDeposito(@RequestBody Deposito deposito) {
        log.info("Creando nuevo depósito");
        Deposito depositoCreado = depositoService.persistirDeposito(deposito);
        log.info("Depósito creado con id {}", depositoCreado.getIdDeposito());
        return ResponseEntity.status(HttpStatus.CREATED).body(depositoCreado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeposito(@PathVariable Integer id) {
        log.info("Eliminando depósito {}", id);
        try {
            depositoService.eliminarDepositoPorId(id);
            log.info("Depósito {} eliminado", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            log.warn("No se encontró depósito {} para eliminar", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}