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

    @JsonProperty("costoReal")
    private Double costoReal;

    @JsonProperty("estado")
    private EstadoRemotoDTO estado;

    @JsonProperty("camion")
    private CamionRemotoDTO camion;

    public boolean isFinalizado() {
        return estado != null && estado.getNombre() != null && "finalizado".equalsIgnoreCase(estado.getNombre());
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EstadoRemotoDTO {

        @JsonProperty("nombre")
        private String nombre;
    }
}
