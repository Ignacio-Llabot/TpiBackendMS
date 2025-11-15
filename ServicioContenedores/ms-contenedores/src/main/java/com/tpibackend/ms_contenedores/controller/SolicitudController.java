package com.tpibackend.ms_contenedores.controller;

import com.tpibackend.ms_contenedores.entity.Solicitud;
import com.tpibackend.ms_contenedores.service.SolicitudService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;


    public SolicitudController(
        SolicitudService solicitudService
    ) {
        this.solicitudService = solicitudService;
    }

    
    // Definimos endpoints

    @PostMapping
    public ResponseEntity<Solicitud> postSolicitud(@RequestBody Solicitud solicitud) {
        solicitud = solicitudService.crearNuevaSolicitud(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
    }

    @GetMapping("{id}")
    public ResponseEntity<Solicitud> getSolicitudPorId(@PathVariable Integer id) {
        Solicitud solicitud = solicitudService.getSolicitudPorId(id);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("{id}")
    public ResponseEntity<Solicitud> modificarSolicitud(@PathVariable Integer id, @RequestBody Solicitud solicitudNueva) {

        // Obtiene
        Solicitud miSolicitud = solicitudService.getSolicitudPorId(id);

        // Actualiza
        miSolicitud.setCliente(solicitudNueva.getCliente());
        miSolicitud.setContenedor(solicitudNueva.getContenedor());
        miSolicitud.setCostoEstimado(solicitudNueva.getCostoEstimado());
        miSolicitud.setCostoFinal(solicitudNueva.getCostoFinal());
        miSolicitud.setEstado(solicitudNueva.getEstado());
        miSolicitud.setTiempoEstimado(solicitudNueva.getTiempoEstimado());
        miSolicitud.setTiempoReal(solicitudNueva.getTiempoReal());


        // Guarda
        miSolicitud = solicitudService.persistirSolicitud(miSolicitud);
        return ResponseEntity.ok(miSolicitud);
    }

    
}
    