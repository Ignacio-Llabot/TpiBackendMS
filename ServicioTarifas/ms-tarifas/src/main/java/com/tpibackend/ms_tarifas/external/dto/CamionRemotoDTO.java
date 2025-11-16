package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CamionRemotoDTO {

    @JsonProperty("patente")
    private String patente;

    @JsonProperty("tipoCamion")
    private TipoCamionRemotoDTO tipoCamion;
}
