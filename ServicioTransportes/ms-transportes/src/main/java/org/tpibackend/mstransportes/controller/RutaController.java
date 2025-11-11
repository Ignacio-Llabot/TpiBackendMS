package org.tpibackend.mstransportes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.RutaService;

import java.sql.Date;

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

    @PostMapping("/{idSolicitud}")
    public ResponseEntity<Ruta> postRutaParaSolicitud(
            @PathVariable Integer idSolicitud,
            @RequestBody String ubicacionesJson
        ) {
        
            try {
                // Parsear el JSON
                JsonNode rootNode = objectMapper.readTree(ubicacionesJson);
                    
                // Extraer ubicacionInicial
                JsonNode inicialNode = rootNode.path("ubicacionInicial");
                Ubicacion ubicacionInicial = objectMapper.treeToValue(inicialNode, Ubicacion.class);
                    
                // Extraer ubicacionFinal
                JsonNode finalNode = rootNode.path("ubicacionFinal");
                Ubicacion ubicacionFinal = objectMapper.treeToValue(finalNode, Ubicacion.class);

                JsonNode fechaHoraInicioNode = rootNode.path("fechaHoraInicio");
                Date fechaHoraInicio = objectMapper.treeToValue(fechaHoraInicioNode, Date.class);
                    
                // Crear la ruta
                Ruta ruta = rutaService.crearRutaParaSolicitud(
                    idSolicitud,
                    ubicacionInicial, 
                    ubicacionFinal,
                    fechaHoraInicio
                );
                    
                return ResponseEntity.ok(ruta);
                    
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
            
    
    
}
