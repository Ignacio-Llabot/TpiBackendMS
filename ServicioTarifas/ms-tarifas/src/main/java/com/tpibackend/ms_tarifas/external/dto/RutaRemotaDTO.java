package com.tpibackend.ms_tarifas.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RutaRemotaDTO {

    @JsonProperty("idRuta")
    private Integer idRuta;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("idSolicitud")
    private Integer idSolicitud;

    @JsonProperty("cantidadTramos")
    private Integer cantidadTramos;

    @JsonProperty("cantidadDepositos")
    private Integer cantidadDepositos;

    @JsonProperty("tramos")
    private List<TramoRemotoDTO> tramos = new ArrayList<>();

    public Integer resolveRutaId() {
        return idRuta != null ? idRuta : id;
    }
}
