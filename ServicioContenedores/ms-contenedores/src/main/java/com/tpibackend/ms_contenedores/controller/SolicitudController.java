package com.tpibackend.ms_contenedores.controller;

import com.tpibackend.ms_contenedores.entity.Solicitud;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.service.SolicitudService;
import com.tpibackend.ms_contenedores.service.EstadoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    private final EstadoService estadoService;

    public SolicitudController(
        SolicitudService solicitudService,
        EstadoService estadoService
    ) {
        this.solicitudService = solicitudService;
        this.estadoService = estadoService;
    }

    
    // Definimos endpoints

    @PostMapping
    public Solicitud postSolicitud(@RequestBody Solicitud solicitud) {
        return solicitudService.crearNuevaSolicitud(solicitud);
    }

    @PutMapping("{id}")
    public Solicitud modificarSolicitud(@PathVariable Integer id, @RequestBody Solicitud solicitudNueva) {

        // Obtiene
        Solicitud miSolicitud = solicitudService.getSolicitudPorId(id);

        // Actualiza
        miSolicitud.setCliente(solicitudNueva.getCliente());
        miSolicitud.setContenedor(solicitudNueva.getContenedor());
        miSolicitud.setCostoEstimado(solicitudNueva.getCostoEstimado());
        miSolicitud.setCostoFinal(solicitudNueva.getCostoFinal());

        // miSolicitud.setEstado(solicitudNueva.getEstado());

        Estado estado = estadoService.getEstadoPorId(solicitudNueva.getEstado().getIdEstado());

        miSolicitud.setTiempoEstimado(solicitudNueva.getTiempoEstimado());
        miSolicitud.setTiempoReal(solicitudNueva.getTiempoReal());


        // Guarda
        return solicitudService.persistirSolicitud(miSolicitud);
        
        }
    }
    