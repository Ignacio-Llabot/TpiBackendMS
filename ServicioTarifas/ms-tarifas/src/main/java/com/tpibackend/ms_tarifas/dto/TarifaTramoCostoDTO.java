package com.tpibackend.ms_tarifas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaTramoCostoDTO {
    private Integer tramoId;
    private Double costoTramo;
}
