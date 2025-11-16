package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoCamionRemotoDTO {

    @JsonProperty("idTipoCamion")
    private Integer idTipoCamion;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("nombre")
    private String nombre;

    public Integer resolveTipoCamionId() {
        return idTipoCamion != null ? idTipoCamion : id;
    }
}
