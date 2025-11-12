package org.tpibackend.mstransportes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.RutaService;
import org.tpibackend.mstransportes.service.osrmstategies.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {
    
    private final RutaService rutaService;
    private final ObjectMapper objectMapper;
    
    public RutaController(RutaService rutaService, ObjectMapper objectMapper) {
        this.rutaService = rutaService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{idSolicitud}/{strategy:(?:urgente|menorcosto|optima)}")
    public ResponseEntity<Ruta> postRutaParaSolicitud(
            @PathVariable Integer idSolicitud,
            @PathVariable String strategy,
            @RequestBody String ubicacionesJson
    ) {

        Strategy estrategia;

        if (strategy.equals("urgente")) {
            estrategia = new UrgenteStrategy();
        } else if (strategy.equals("menorcosto")) {
            estrategia = new MenorCostoStrategy();
        } else if (strategy.equals("optima")) {
            estrategia = new OptimaStrategy();
        } else {
            return ResponseEntity.badRequest().build();
        }

        rutaService.setStrategyOsrmService(estrategia);
        try {
            JsonNode rootNode = objectMapper.readTree(ubicacionesJson);

            Ubicacion ubicacionInicial = objectMapper.treeToValue(rootNode.path("ubicacionInicial"), Ubicacion.class);
            Ubicacion ubicacionFinal = objectMapper.treeToValue(rootNode.path("ubicacionFinal"), Ubicacion.class);
            LocalDateTime fechaHoraInicio = objectMapper.treeToValue(rootNode.path("fechaHoraInicio"), LocalDateTime.class);

            Ruta ruta = rutaService.crearRutasParaSolicitud(
                    idSolicitud,
                    ubicacionInicial,
                    ubicacionFinal,
                    fechaHoraInicio
                );
                    
                return null; //ResponseEntity.ok(ruta);
                    
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
    }
            
    
    
}
