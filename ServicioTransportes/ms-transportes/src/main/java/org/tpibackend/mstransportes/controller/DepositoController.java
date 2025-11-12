package org.tpibackend.mstransportes.controller;

import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.DepositoService;
import org.tpibackend.mstransportes.service.UbicacionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/depositos")

public class DepositoController {

    private final DepositoService depositoService;
    private final UbicacionService ubicacionService;

    public DepositoController(DepositoService depositoService, UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
        this.depositoService = depositoService;
    }

    // Definimos endpoints

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> getDepositoPorId(@PathVariable Integer id) {
        Deposito deposito = depositoService.getDepositoPorId(id);
        return ResponseEntity.ok(deposito); // 200 OK
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> putDeposito(@PathVariable Integer id, @RequestBody Deposito deposito) {
        // Obtiene
        Deposito miDeposito = depositoService.getDepositoPorId(id);

        // Actualiza
        miDeposito.setCostoEstadia(deposito.getCostoEstadia());

        if (deposito.getUbicacion() != null && deposito.getUbicacion().getIdUbicacion() != null) {
            Ubicacion nuevaUbicacion = ubicacionService.getUbicacionPorId(deposito.getUbicacion().getIdUbicacion());
            miDeposito.setUbicacion(nuevaUbicacion);
}

        // Guarda
        return ResponseEntity.ok(depositoService.persistirDeposito(miDeposito)); // 200 OK
    }


    @PostMapping
    public ResponseEntity<Deposito> postDeposito(@RequestBody Deposito deposito) {
        Deposito depositoCreado = depositoService.persistirDeposito(deposito);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositoCreado); // 201 Created
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeposito(@PathVariable Integer id) {
        depositoService.eliminarDepositoPorId(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}