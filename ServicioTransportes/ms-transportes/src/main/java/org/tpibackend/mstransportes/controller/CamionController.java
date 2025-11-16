package org.tpibackend.mstransportes.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import org.tpibackend.mstransportes.dto.CamionResumenDTO;
import org.tpibackend.mstransportes.entity.Camion;
import org.tpibackend.mstransportes.service.CamionService;
import org.tpibackend.mstransportes.service.TipoCamionService;
import org.tpibackend.mstransportes.service.TransportistaService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/camiones")
public class CamionController {

    private final CamionService camionService;
    private final TipoCamionService tipoCamionService;
    private final TransportistaService transportistaService;
    private static final Logger log = LoggerFactory.getLogger(CamionController.class);

    public CamionController(CamionService camionService, TipoCamionService tipoCamionService,
            TransportistaService transportistaService) {
        this.camionService = camionService;
        this.tipoCamionService = tipoCamionService;
        this.transportistaService = transportistaService;
    }

    // Definimos endpoints

    @GetMapping
    public ResponseEntity<List<CamionResumenDTO>> getCamiones() {
        log.info("Recuperando listado de camiones");
        List<CamionResumenDTO> camiones = camionService.getCamiones()
            .stream()
            .map(camion -> new CamionResumenDTO(
                camion.getPatente(),
                camion.getTipoCamion() != null ? camion.getTipoCamion().getIdTipoCamion() : null,
                camion.getCapacidadPeso(),
                camion.getCapacidadVolumen()
            ))
            .collect(Collectors.toList());
        log.info("Listado de camiones obtenido con {} registros", camiones.size());
        return ResponseEntity.ok(camiones);
    }

    @GetMapping("/{patente}")
    public ResponseEntity<Camion> getCamionPorPatente(@PathVariable("patente") String patente) {
        log.info("Consultando camión {}", patente);
        try {
            Camion camion = camionService.getCamionPorPatente(patente);
            log.info("Camión {} recuperado", patente);
            return ResponseEntity.ok(camion);
        } catch (EntityNotFoundException ex) {
            log.warn("Camión {} no encontrado", patente);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{patente}")
    public ResponseEntity<Camion> putCamion(@PathVariable("patente") String patente, @RequestBody Camion camion) {
        log.info("Actualizando camión {}", patente);
        try {
            Camion miCamion = camionService.getCamionPorPatente(patente);
            miCamion.setCapacidadPeso(camion.getCapacidadPeso());
            miCamion.setTipoCamion(
                tipoCamionService.getTipoCamionPorId(camion.getTipoCamion().getIdTipoCamion()));
            miCamion.setTransportista(
                transportistaService.getTransportistaPorId(camion.getTransportista().getIdTransportista()));

            Camion actualizado = camionService.persistirCamion(miCamion);
            log.info("Camión {} actualizado", patente);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException ex) {
            log.warn("No se pudo actualizar el camión {}: {}", patente, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Camion> postCamion(@RequestBody Camion camion) {
        log.info("Creando nuevo camión");
        Camion camionCreado = camionService.persistirCamion(camion);
        log.info("Camión creado con patente {}", camionCreado.getPatente());
        return ResponseEntity.status(HttpStatus.CREATED).body(camionCreado);
    }
    
    @DeleteMapping("/{patente}")
    public ResponseEntity<Void> deleteCamion(@PathVariable("patente") String patente) {
        log.info("Eliminando camión {}", patente);
        try {
            camionService.eliminarCamionPorPatente(patente);
            log.info("Camión {} eliminado", patente);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            log.warn("No se encontró camión {} para eliminar", patente);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
}
