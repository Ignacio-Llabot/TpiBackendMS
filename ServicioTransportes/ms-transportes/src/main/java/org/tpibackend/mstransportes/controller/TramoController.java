package org.tpibackend.mstransportes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpibackend.mstransportes.service.TramoService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping("/api/v1/tramos")
public class TramoController {

	private final TramoService tramoService;

	public TramoController(TramoService tramoService) {
		this.tramoService = tramoService;
	}

	@PutMapping("/{solicitudId}/{tramoId}/{patenteCamion}")
	public ResponseEntity<Void> asignarCamionATramo(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId,
		@PathVariable String patenteCamion
	) {
		tramoService.asignarCamionATramo(solicitudId, tramoId, patenteCamion);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{solicitudId}/{tramoId}/encamino")
	public ResponseEntity<Void> marcarTramoEnCamino(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId
	) {
		tramoService.marcarTramoEnCamino(solicitudId, tramoId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{solicitudId}/{tramoId}/finalizado")
	public ResponseEntity<Void> marcarTramoFinalizado(
		@PathVariable Integer solicitudId,
		@PathVariable Integer tramoId
	) {
		tramoService.marcarTramoFinalizado(solicitudId, tramoId);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> manejarNoEncontrado(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> manejarEstadoInvalido(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}
