package com.tpibackend.ms_tarifas.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCostoFinalRequest {
    private Double costoFinal;
}
