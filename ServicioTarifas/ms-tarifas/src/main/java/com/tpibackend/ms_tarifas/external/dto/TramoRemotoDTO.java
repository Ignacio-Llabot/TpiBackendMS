package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramoRemotoDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("distancia")
    private Double distancia;

    @JsonProperty("costoAproximado")
    private Double costoAproximado;

    @JsonProperty("camion")
    private CamionRemotoDTO camion;
}
