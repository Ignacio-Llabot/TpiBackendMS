package com.tpibackend.ms_contenedores.controller;

import com.tpibackend.ms_contenedores.dto.ContenedorUbicacionDTO;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import com.tpibackend.ms_contenedores.entity.Estado;
import com.tpibackend.ms_contenedores.service.ContenedorService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }


    // Definimos endpoints

    @GetMapping("{id}/estado")
    public ResponseEntity<Estado> getEstadoContenedor(@PathVariable Integer id){
        try {
            Estado estado = contenedorService.getEstadoContenedor(id);
            return ResponseEntity.ok(estado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    

    @PostMapping
    public ResponseEntity<Contenedor> postContenedor(@RequestBody Contenedor contenedor) {
        Contenedor contenedorCreado = contenedorService.persistirContenedor(contenedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(contenedorCreado);
    }

    @GetMapping("/trackingPend")
    public ResponseEntity<List<ContenedorUbicacionDTO>> getContenedoresPendientesUbicacion() {
        List<ContenedorUbicacionDTO> contenedores = contenedorService.getContenedoresPendientes();
        return ResponseEntity.ok(contenedores);
    }


}