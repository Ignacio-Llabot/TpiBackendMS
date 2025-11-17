package com.tpibackend.ms_contenedores.controller;

import java.util.List;

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

import com.tpibackend.ms_contenedores.dto.ActualizarEstadoContenedorRequest;
import com.tpibackend.ms_contenedores.dto.ContenedorUbicacionDTO;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.service.ContenedorService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;
    private static final Logger log = LoggerFactory.getLogger(ContenedorController.class);

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }


    // Definimos endpoints

    @GetMapping("{id}/estado")
    public ResponseEntity<Estado> getEstadoContenedor(@PathVariable Integer id){
        log.info("Consultando estado del contenedor {}", id);
        try {
            Estado estado = contenedorService.getEstadoContenedor(id);
            log.info("Estado del contenedor {} recuperado", id);
            return ResponseEntity.ok(estado);
        } catch (EntityNotFoundException e) {
            log.warn("Contenedor {} no encontrado: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    

    @PostMapping
    public ResponseEntity<Contenedor> postContenedor(@RequestBody Contenedor contenedor) {
        log.info("Creando contenedor - inicio del procesamiento");
        Contenedor contenedorCreado = contenedorService.persistirContenedor(contenedor);
        log.info("Contenedor creado con id {}", contenedorCreado.getIdContenedor());
        return ResponseEntity.status(HttpStatus.CREATED).body(contenedorCreado);
    }

    @PutMapping("{id}/estado")
    public ResponseEntity<Void> actualizarEstadoContenedor(
        @PathVariable Integer id,
        @RequestBody ActualizarEstadoContenedorRequest request
    ) {
        log.info("Actualizando estado del contenedor {} a {}", id, request.getEstado());
        contenedorService.actualizarEstado(id, request.getEstado());
        log.info("Estado del contenedor {} actualizado", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trackingPend")
    public ResponseEntity<List<ContenedorUbicacionDTO>> getContenedoresPendientesUbicacion() {
        log.info("Consultando contenedores pendientes para tracking");
        List<ContenedorUbicacionDTO> contenedores = contenedorService.getContenedoresPendientes();
        log.info("Respuesta de tracking generada con {} contenedores", contenedores.size());
        return ResponseEntity.ok(contenedores);
    }


}