package com.tpibackend.ms_tarifas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromedioAtributosDTO {
    private Double promedioCostoBaseXKm;
    private Double promedioValorLitroCombustible;
    private Double promedioConsumoCombustibleGeneral;
}
