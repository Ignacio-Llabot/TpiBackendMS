package org.tpibackend.mstransportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCostoTramoRequest {
    private Double incrementoCosto;
}
