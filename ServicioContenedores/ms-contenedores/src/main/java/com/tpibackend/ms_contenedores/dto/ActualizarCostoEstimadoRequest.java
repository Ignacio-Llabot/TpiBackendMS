package com.tpibackend.ms_contenedores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCostoEstimadoRequest {
    private Double costoEstimado;
}
