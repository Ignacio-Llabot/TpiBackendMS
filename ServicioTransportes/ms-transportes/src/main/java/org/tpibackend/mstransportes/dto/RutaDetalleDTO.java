package org.tpibackend.mstransportes.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpibackend.mstransportes.entity.Estado;
import org.tpibackend.mstransportes.entity.TipoTramo;
import org.tpibackend.mstransportes.entity.Ubicacion;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaDetalleDTO {

    private Integer id;
    private Integer idSolicitud;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private Ubicacion ubicacionInicial;
    private Ubicacion ubicacionFinal;
    @Builder.Default
    private List<TramoDetalleDTO> tramos = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TramoDetalleDTO {
        private Integer id;
        private Ubicacion ubicacionOrigen;
        private Ubicacion ubicacionDestino;
        private Double distancia;
        private TipoTramo tipoTramo;
        private Estado estado;
        private Double costoAproximado;
        private Double costoReal;
        private LocalDateTime fechaHoraInicioEstimada;
        private LocalDateTime fechaHoraFinEstimada;
        private LocalDateTime fechaHoraInicio;
        private LocalDateTime fechaHoraFin;
        private CamionDetalleDTO camion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CamionDetalleDTO {
        private String patente;
        private TipoCamionDetalleDTO tipoCamion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoCamionDetalleDTO {
        private Integer id;
        private String nombre;
    }
}
