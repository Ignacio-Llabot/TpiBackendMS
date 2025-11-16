package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudRemotaDTO {

    @JsonProperty("idSolicitud")
    private Integer idSolicitud;

    @JsonProperty("contenedor")
    private ContenedorRemotoDTO contenedor;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContenedorRemotoDTO {

        @JsonProperty("idContenedor")
        private Integer idContenedor;

        @JsonProperty("volumen")
        private Double volumen;

        @JsonProperty("peso")
        private Double peso;
    }
}
