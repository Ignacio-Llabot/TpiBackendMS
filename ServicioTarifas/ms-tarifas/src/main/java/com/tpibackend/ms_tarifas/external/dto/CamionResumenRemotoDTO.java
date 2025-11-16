package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CamionResumenRemotoDTO {

    @JsonProperty("patente")
    private String patente;

    @JsonProperty("tipoCamionId")
    private Integer tipoCamionId;

    @JsonProperty("capacidadPeso")
    private Double capacidadPeso;

    @JsonProperty("capacidadVolumen")
    private Double capacidadVolumen;
}
