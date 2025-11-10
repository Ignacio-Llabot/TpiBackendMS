package org.tpibackend.mstransportes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"Tramos\"")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tramo {

    // TODO revisar los nullable están horrendos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idTramo\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idRuta\"", nullable = false)
    private Ruta ruta;

    @Column(name = "\"distancia\"", nullable = false)
    private Double distancia;

    @Column(name = "\"costoAproximado\"", nullable = false)
    private Double costoAproximado;

    @Column(name = "\"costoReal\"")
    private Double costoReal;

    @Column(name = "\"fechaHoraInicioEstimada\"", nullable = false)
    private LocalDateTime fechaHoraInicioEstimada;

    @Column(name = "\"fechaHoraFinEstimada\"", nullable = false)
    private LocalDateTime fechaHoraFinEstimada;

    @Column(name = "\"fechaHoraInicio\"")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "\"fechaHoraFin\"")
    private LocalDateTime fechaHoraFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUbicacionOrigen\"", nullable = false)
    private Ubicacion ubicacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUbicacionDestino\"", nullable = false)
    private Ubicacion ubicacionDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idTipoTramo\"", nullable = false)
    private TipoTramo tipoTramo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idEstado\"", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"patenteCamion\"", nullable = false)
    private Camion camion;
}