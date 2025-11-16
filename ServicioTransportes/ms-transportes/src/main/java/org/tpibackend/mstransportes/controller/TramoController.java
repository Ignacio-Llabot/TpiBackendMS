package org.tpibackend.mstransportes.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.dto.ActualizarCostoRealTramoRequest;
import org.tpibackend.mstransportes.dto.ActualizarCostoTramoRequest;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.service.TramoService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/tramos")
public class TramoController {

	private final TramoService tramoService;
    private static final Logger log = LoggerFactory.getLogger(TramoController.class);

	public TramoController(TramoService tramoService) {
		this.tramoService = tramoService;
	}

	@GetMapping("/{rutaId:\\d+}")
	public ResponseEntity<List<Tramo>> getTramosPorRuta(@PathVariable Integer rutaId) {
		log.info("Obteniendo tramos para ruta {}", rutaId);
		List<Tramo> tramos = tramoService.getTramosPorRuta(rutaId);
		log.info("Ruta {} contiene {} tramos", rutaId, tramos.size());
		return ResponseEntity.ok(tramos);
	}

	@PutMapping("/{tramoId:\\d+}/costo-aproximado")
	public ResponseEntity<Void> actualizarCostoAproximado(
		@PathVariable Integer tramoId,
		@RequestBody ActualizarCostoTramoRequest request
	) {
		log.info("Actualizando costo aproximado del tramo {}", tramoId);
		tramoService.actualizarCostoAproximado(tramoId, request.getIncrementoCosto());
		log.info("Costo aproximado del tramo {} actualizado", tramoId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{tramoId:\\d+}/costo-real")
	public ResponseEntity<Void> actualizarCostoReal(
		@PathVariable Integer tramoId,
		@RequestBody ActualizarCostoRealTramoRequest request
	) {
		log.info("Actualizando costo real del tramo {}", tramoId);
		tramoService.actualizarCostoReal(tramoId, request.getCostoReal());
		log.info("Costo real del tramo {} actualizado", tramoId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{solicitudId}/{tramoId}/{patenteCamion}")
	public ResponseEntity<Void> asignarCamionATramo(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId,
		@PathVariable String patenteCamion
	) {
		log.info("Asignando camión {} al tramo {} de la solicitud {}", patenteCamion, tramoId, solicitudId);
		tramoService.asignarCamionATramo(solicitudId, tramoId, patenteCamion);
		log.info("Camión {} asignado al tramo {} de la solicitud {}", patenteCamion, tramoId, solicitudId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{solicitudId}/{tramoId}/encamino")
	public ResponseEntity<Void> marcarTramoEnCamino(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId
	) {
		log.info("Marcando tramo {} de la solicitud {} como en camino", tramoId, solicitudId);
		tramoService.marcarTramoEnCamino(solicitudId, tramoId);
		log.info("Tramo {} de la solicitud {} marcado como en camino", tramoId, solicitudId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{solicitudId}/{tramoId}/finalizado")
	public ResponseEntity<Void> marcarTramoFinalizado(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId
	) {
		log.info("Marcando tramo {} de la solicitud {} como finalizado", tramoId, solicitudId);
		tramoService.marcarTramoFinalizado(solicitudId, tramoId);
		log.info("Tramo {} de la solicitud {} marcado como finalizado", tramoId, solicitudId);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> manejarNoEncontrado(EntityNotFoundException ex) {
		log.warn("Entidad no encontrada: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> manejarEstadoInvalido(IllegalStateException ex) {
		log.warn("Estado inválido al operar con tramos: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}
