package org.tpibackend.mstransportes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.RutaService;
import org.tpibackend.mstransportes.service.osrmstategies.*;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
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

            JsonNode fechaHoraNode = rootNode.path("fechaHoraInicio");
            if (fechaHoraNode.isMissingNode() || fechaHoraNode.isNull()) {
                return ResponseEntity.badRequest().build();
            }
            LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraNode.asText());

            Ruta ruta = rutaService.crearRutasParaSolicitud(
                    idSolicitud,
                    ubicacionInicial,
                    ubicacionFinal,
                    fechaHoraInicio
                );
                    
                return ResponseEntity.status(HttpStatus.CREATED).body(ruta); // 201 Created
                    
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // error acá
            }
        }

        @GetMapping("/{idRuta}")
        public ResponseEntity<Ruta> getRutaPorId(@PathVariable Integer idRuta) {
            Ruta ruta = rutaService.getRutaPorId(idRuta);
            if (ruta != null) {
                return ResponseEntity.ok(ruta);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    
    @GetMapping("/{idRuta}/detalle")
    public ResponseEntity<RutaDetalleDTO> getRutaDetallada(@PathVariable Integer idRuta) {
        try {
            RutaDetalleDTO detalle = rutaService.obtenerRutaDetallada(idRuta);
            return ResponseEntity.ok(detalle);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    }

