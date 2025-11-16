package org.tpibackend.mstransportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionResumenDTO {
    private String patente;
    private Integer tipoCamionId;
    private Double capacidadPeso;
    private Double capacidadVolumen;
}
