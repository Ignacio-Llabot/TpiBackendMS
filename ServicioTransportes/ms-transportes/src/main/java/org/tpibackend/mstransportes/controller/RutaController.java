package org.tpibackend.mstransportes.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.RutaService;
import org.tpibackend.mstransportes.service.osrmstategies.MenorCostoStrategy;
import org.tpibackend.mstransportes.service.osrmstategies.OptimaStrategy;
import org.tpibackend.mstransportes.service.osrmstategies.Strategy;
import org.tpibackend.mstransportes.service.osrmstategies.UrgenteStrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {

    private static final Logger log = LoggerFactory.getLogger(RutaController.class);

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
        log.info("Creando ruta para solicitud {} con estrategia {}", idSolicitud, strategy);

        Strategy estrategia;
        if ("urgente".equals(strategy)) {
            estrategia = new UrgenteStrategy();
        } else if ("menorcosto".equals(strategy)) {
            estrategia = new MenorCostoStrategy();
        } else if ("optima".equals(strategy)) {
            estrategia = new OptimaStrategy();
        } else {
            log.warn("Estrategia {} no soportada", strategy);
            return ResponseEntity.badRequest().build();
        }

        rutaService.setStrategyOsrmService(estrategia);

        try {
            JsonNode rootNode = objectMapper.readTree(ubicacionesJson);
            log.debug("Payload de ubicaciones recibido: {}", rootNode);

            Ubicacion ubicacionInicial = objectMapper.treeToValue(rootNode.path("ubicacionInicial"), Ubicacion.class);
            Ubicacion ubicacionFinal = objectMapper.treeToValue(rootNode.path("ubicacionFinal"), Ubicacion.class);

            JsonNode fechaHoraNode = rootNode.path("fechaHoraInicio");
            if (fechaHoraNode.isMissingNode() || fechaHoraNode.isNull()) {
                log.warn("Solicitud {} sin fecha de inicio", idSolicitud);
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraNode.asText());

            Ruta ruta = rutaService.crearRutasParaSolicitud(
                idSolicitud,
                ubicacionInicial,
                ubicacionFinal,
                fechaHoraInicio
            );
            log.info("Ruta {} creada para solicitud {}", ruta.getIdRuta(), idSolicitud);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);
        } catch (Exception ex) {
            log.error("Error creando ruta para solicitud {}", idSolicitud, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idRuta}")
    public ResponseEntity<Ruta> getRutaPorId(@PathVariable Integer idRuta) {
        log.info("Obteniendo ruta {}", idRuta);
        try {
            Ruta ruta = rutaService.getRutaPorId(idRuta);
            log.info("Ruta {} recuperada", idRuta);
            return ResponseEntity.ok(ruta);
        } catch (EntityNotFoundException ex) {
            log.warn("Ruta {} no encontrada", idRuta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{idRuta}/detalle")
    public ResponseEntity<RutaDetalleDTO> getRutaDetallada(@PathVariable Integer idRuta) {
        log.info("Obteniendo detalle para ruta {}", idRuta);
        try {
            RutaDetalleDTO detalle = rutaService.obtenerRutaDetallada(idRuta);
            log.info("Detalle de ruta {} recuperado", idRuta);
            return ResponseEntity.ok(detalle);
        } catch (EntityNotFoundException ex) {
            log.warn("Ruta {} no encontrada al solicitar detalle", idRuta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

