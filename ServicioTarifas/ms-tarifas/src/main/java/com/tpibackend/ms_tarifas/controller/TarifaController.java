package com.tpibackend.ms_tarifas.controller;


import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.service.TarifaService;

import jakarta.persistence.EntityNotFoundException;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/tarifas")
public class TarifaController {
    
    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Tarifa> getTarifa(@PathVariable Integer id) {
        try {
            Tarifa tarifa = tarifaService.getTarifaPorId(id);
            return ResponseEntity.ok(tarifa);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Tarifa> postTarifa(@RequestBody Tarifa tarifa) {
        Tarifa tarifaCreada = tarifaService.persistirTarifa(tarifa);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaCreada);
    }

    @PutMapping("{id}")
    public ResponseEntity<Tarifa> putTarifa(@PathVariable Integer id, @RequestBody Tarifa tarifa) {
        try {
            Tarifa miTarifa = tarifaService.getTarifaPorId(id);

            miTarifa.setConsumoCombustibleGeneral(tarifa.getConsumoCombustibleGeneral());
            miTarifa.setCostoBaseXKm(tarifa.getCostoBaseXKm());
            miTarifa.setValorLitroCombustible(tarifa.getValorLitroCombustible());
            
            // Aca le reemplazo por el id del tipo de camion que voy a querer, pero no se como seria para que dsp reemplace el id por el camion, o si el q postea la tarifa tiene q mandar el id del camion
            miTarifa.setTipoCamion(tarifa.getTipoCamion());

            Tarifa tarifaActualizada = tarifaService.persistirTarifa(tarifa);
            return ResponseEntity.ok(tarifaActualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Integer id) {
        try {
            tarifaService.eliminarTarifaPorId(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}