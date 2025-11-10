package org.tpibackend.mstransportes.dto;

import org.tpibackend.mstransportes.entity.Ubicacion;
import lombok.Data;

@Data
public class TramoDTO {
    private Ubicacion ubicacionOrigen;
    private Ubicacion ubicacionDestino;
    private Double distancia; // metros
    private Double duracionEstimada; // segundos
}
