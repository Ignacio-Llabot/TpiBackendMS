package com.tpibackend.ms_tarifas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaAproximadaDetalleDTO {
    private Integer tipoCamionId;
    private String tipoCamionDescripcion;
    private Double costoBasePorKilometro;
    private Double valorLitroCombustiblePromedio;
    private Double consumoCombustiblePromedio;
    private Double costoBaseTotal;
    private Double costoCombustibleTotal;
    private Double costoEstadiaTotal;
    private Double costoTotal;
}
