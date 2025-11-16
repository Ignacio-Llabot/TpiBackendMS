package com.tpibackend.ms_contenedores.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpibackend.ms_contenedores.dto.ActualizarCostoEstimadoRequest;
import com.tpibackend.ms_contenedores.dto.ActualizarCostoFinalRequest;
import com.tpibackend.ms_contenedores.entity.Solicitud;
import com.tpibackend.ms_contenedores.service.SolicitudService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private static final Logger log = LoggerFactory.getLogger(SolicitudController.class);


    public SolicitudController(
        SolicitudService solicitudService
    ) {
        this.solicitudService = solicitudService;
    }

    
    // Definimos endpoints

    @PostMapping
    public ResponseEntity<Solicitud> postSolicitud(@RequestBody Solicitud solicitud) {
        log.info("Creando solicitud - inicio del procesamiento");
        solicitud = solicitudService.crearNuevaSolicitud(solicitud);
        log.info("Creando solicitud - finalizado con id {}", solicitud.getIdSolicitud());
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
    }

    @GetMapping("{id}")
    public ResponseEntity<Solicitud> getSolicitudPorId(@PathVariable Integer id) {
        log.info("Recuperando solicitud {}", id);
        try {
            Solicitud solicitud = solicitudService.getSolicitudPorId(id);
            log.info("Solicitud {} recuperada correctamente", id);
            return ResponseEntity.ok(solicitud);
        } catch (EntityNotFoundException ex) {
            log.warn("Solicitud {} no encontrada: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Solicitud> modificarSolicitud(@PathVariable Integer id, @RequestBody Solicitud solicitudNueva) {
        log.info("Actualizando solicitud {}", id);

        try {
            Solicitud miSolicitud = solicitudService.getSolicitudPorId(id);

            miSolicitud.setCliente(solicitudNueva.getCliente());
            miSolicitud.setContenedor(solicitudNueva.getContenedor());
            miSolicitud.setCostoEstimado(solicitudNueva.getCostoEstimado());
            miSolicitud.setCostoFinal(solicitudNueva.getCostoFinal());
            miSolicitud.setEstado(solicitudNueva.getEstado());
            miSolicitud.setTiempoEstimado(solicitudNueva.getTiempoEstimado());
            miSolicitud.setTiempoReal(solicitudNueva.getTiempoReal());


            miSolicitud = solicitudService.persistirSolicitud(miSolicitud);
            log.info("Solicitud {} actualizada", id);
            return ResponseEntity.ok(miSolicitud);
        } catch (EntityNotFoundException ex) {
            log.warn("No se pudo actualizar la solicitud {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("{id}/costo-estimado")
    public ResponseEntity<Void> actualizarCostoEstimado(
        @PathVariable Integer id,
        @RequestBody ActualizarCostoEstimadoRequest request
    ) {
        log.info("Actualizando costo estimado de la solicitud {}", id);
        solicitudService.actualizarCostoEstimado(id, request.getCostoEstimado());
        log.info("Costo estimado actualizado para la solicitud {}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/costo-final")
    public ResponseEntity<Void> actualizarCostoFinal(
        @PathVariable Integer id,
        @RequestBody ActualizarCostoFinalRequest request
    ) {
        log.info("Actualizando costo final de la solicitud {}", id);
        solicitudService.actualizarCostoFinal(id, request.getCostoFinal());
        log.info("Costo final actualizado para la solicitud {}", id);
        return ResponseEntity.noContent().build();
    }

    
}
    