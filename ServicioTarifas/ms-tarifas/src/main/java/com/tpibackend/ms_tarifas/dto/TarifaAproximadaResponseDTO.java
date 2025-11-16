package com.tpibackend.ms_tarifas.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaAproximadaResponseDTO {
    private Integer rutaId;
    private Integer solicitudId;
    private Double volumenContenedor;
    private Double pesoContenedor;
    private Double distanciaTotal;
    private Integer cantidadDepositos;
    private Double costoEstadiaAcumulado;
    private Double costoTotalRuta;
    private List<TarifaTramoCostoDTO> costosTramos;
}
