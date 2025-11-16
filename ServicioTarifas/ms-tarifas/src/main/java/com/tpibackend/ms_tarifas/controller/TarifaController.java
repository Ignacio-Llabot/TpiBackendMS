package com.tpibackend.ms_tarifas.controller;


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

import com.tpibackend.ms_tarifas.dto.TarifaAproximadaResponseDTO;
import com.tpibackend.ms_tarifas.dto.TarifaRealResponseDTO;
import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.service.TarifaService;

import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/api/v1/tarifas")
public class TarifaController {
    
    private final TarifaService tarifaService;
    private static final Logger log = LoggerFactory.getLogger(TarifaController.class);

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Tarifa> getTarifa(@PathVariable Integer id) {
        try {
            log.info("Consultando tarifa {}", id);
            Tarifa tarifa = tarifaService.getTarifaPorId(id);
            if (tarifa == null) {
                log.warn("Tarifa {} no encontrada", id);
            } else {
                log.info("Tarifa {} recuperada", id);
            }
            return ResponseEntity.ok(tarifa);

        } catch (EntityNotFoundException e) {
            log.warn("Tarifa {} no encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/tarifasAproximadas/{idRuta:\\d+}")
    public ResponseEntity<TarifaAproximadaResponseDTO> postTarifaAproximada(@PathVariable Integer idRuta) {
        try {
            log.info("Calculando tarifa aproximada para ruta {}", idRuta);
            TarifaAproximadaResponseDTO respuesta = tarifaService.calcularTarifaAproximada(idRuta);
            log.info("Tarifa aproximada calculada para ruta {}", idRuta);
            return ResponseEntity.ok(respuesta);
        } catch (EntityNotFoundException e) {
            log.warn("No se pudo calcular tarifa aproximada para ruta {}: {}", idRuta, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.warn("Estado invalido calculando tarifa aproximada para ruta {}: {}", idRuta, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/tarifasReales/{idRuta:\\d+}")
    public ResponseEntity<TarifaRealResponseDTO> postTarifaReal(@PathVariable Integer idRuta) {
        try {
            log.info("Calculando tarifa real para ruta {}", idRuta);
            TarifaRealResponseDTO respuesta = tarifaService.calcularTarifaReal(idRuta);
            log.info("Tarifa real calculada para ruta {}", idRuta);
            return ResponseEntity.ok(respuesta);
        } catch (EntityNotFoundException e) {
            log.warn("No se pudo calcular tarifa real para ruta {}: {}", idRuta, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.warn("Estado invalido calculando tarifa real para ruta {}: {}", idRuta, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping
    public ResponseEntity<Tarifa> postTarifa(@RequestBody Tarifa tarifa) {
        log.info("Creando nueva tarifa");
        Tarifa tarifaCreada = tarifaService.persistirTarifa(tarifa);
        log.info("Tarifa creada con id {}", tarifaCreada.getIdTarifa());
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaCreada);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Tarifa> putTarifa(@PathVariable Integer id, @RequestBody Tarifa tarifa) {
        try {
            log.info("Actualizando tarifa {}", id);
            Tarifa miTarifa = tarifaService.getTarifaPorId(id);

            miTarifa.setConsumoCombustibleGeneral(tarifa.getConsumoCombustibleGeneral());
            miTarifa.setCostoBaseXKm(tarifa.getCostoBaseXKm());
            miTarifa.setValorLitroCombustible(tarifa.getValorLitroCombustible());
            
            // Aca le reemplazo por el id del tipo de camion que voy a querer, pero no se como seria para que dsp reemplace el id por el camion, o si el q postea la tarifa tiene q mandar el id del camion
            miTarifa.setTipoCamion(tarifa.getTipoCamion());

            Tarifa tarifaActualizada = tarifaService.persistirTarifa(miTarifa);
                        log.info("Tarifa {} actualizada", id);
            return ResponseEntity.ok(tarifaActualizada);
        } catch (EntityNotFoundException e) {
                        log.warn("Tarifa {} no encontrada para actualizacion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Integer id) {
        try {
                        log.info("Eliminando tarifa {}", id);
            tarifaService.eliminarTarifaPorId(id);
                        log.info("Tarifa {} eliminada", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
                        log.warn("Tarifa {} no encontrada para eliminacion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}