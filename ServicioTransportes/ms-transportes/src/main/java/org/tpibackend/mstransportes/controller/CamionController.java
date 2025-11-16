package org.tpibackend.mstransportes.controller;

import java.util.List;
import java.util.stream.Collectors;
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
import org.tpibackend.mstransportes.dto.CamionResumenDTO;
import org.tpibackend.mstransportes.entity.Camion;
import org.tpibackend.mstransportes.service.CamionService;
import org.tpibackend.mstransportes.service.TipoCamionService;
import org.tpibackend.mstransportes.service.TransportistaService;

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

    @GetMapping
    public ResponseEntity<List<CamionResumenDTO>> getCamiones() {
        List<CamionResumenDTO> camiones = camionService.getCamiones()
            .stream()
            .map(camion -> new CamionResumenDTO(
                camion.getPatente(),
                camion.getTipoCamion() != null ? camion.getTipoCamion().getIdTipoCamion() : null,
                camion.getCapacidadPeso(),
                camion.getCapacidadVolumen()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(camiones);
    }

    @GetMapping("/{patente}")
    public ResponseEntity<Camion> getCamionPorPatente(@PathVariable("patente") String patente) {
        Camion camion = camionService.getCamionPorPatente(patente);
        return ResponseEntity.ok(camion); // 200 OK
    }

    @PutMapping("/{patente}")
    public ResponseEntity<Camion> putCamion(@PathVariable("patente") String patente, @RequestBody Camion camion) {

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
    
    @DeleteMapping("/{patente}")
    public ResponseEntity<Void> deleteCamion(@PathVariable("patente") String patente) {
        camionService.eliminarCamionPorPatente(patente);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    
}
