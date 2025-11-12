package org.tpibackend.mstransportes.controller;

import org.tpibackend.mstransportes.entity.Camion;

import org.tpibackend.mstransportes.service.CamionService;
import org.tpibackend.mstransportes.service.TipoCamionService;
import org.tpibackend.mstransportes.service.TransportistaService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/camiones")
public class CamionController {

    private final CamionService camionService;
    private final TipoCamionService tipoCamionService;
    private final TransportistaService transportistaService;

    public CamionController(CamionService camionService, TipoCamionService tipoCamionService,
            TransportistaService transportistaService) {
        this.camionService = camionService;
        this.tipoCamionService = tipoCamionService;
        this.transportistaService = transportistaService;
    }

    // Definimos endpoints

    @GetMapping("/{id}")
    public ResponseEntity<Camion> getCamionPorPatente(@PathVariable String patente) {
        Camion camion = camionService.getCamionPorPatente(patente);
        return ResponseEntity.ok(camion); // 200 OK
    }

    @PutMapping("/{id}")
    public ResponseEntity<Camion> putCamion(@PathVariable String patente, @RequestBody Camion camion) {

        // Obtiene
        Camion miCamion = camionService.getCamionPorPatente(patente);

        // Actualiza
        miCamion.setCapacidadPeso(camion.getCapacidadPeso());
        miCamion.setTipoCamion(
                tipoCamionService.getTipoCamionPorId(camion.getTipoCamion().getIdTipoCamion()));
        miCamion.setTransportista(
                transportistaService.getTransportistaPorId(camion.getTransportista().getIdTransportista()));

        // Guarda
        return ResponseEntity.ok(camionService.persistirCamion(miCamion)); // 200 OK
    }

    @PostMapping
    public ResponseEntity<Camion> postCamion(@RequestBody Camion camion) {
        Camion camionCreado = camionService.persistirCamion(camion);
        return ResponseEntity.status(HttpStatus.CREATED).body(camionCreado); // 201 Created
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamion(@PathVariable String patente) {
        camionService.eliminarCamionPorPatente(patente);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    
}
