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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idTramo\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idRuta\"", nullable = false)
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "\"idUbicacionOrigen\"", nullable = false)
    private Ubicacion ubicacionOrigen;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "\"idUbicacionDestino\"", nullable = false)
    private Ubicacion ubicacionDestino;

    @Column(name = "distancia", nullable = false)
    private Double distancia;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "\"idTipoTramo\"", nullable = false)
    private TipoTramo tipoTramo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "\"idEstado\"", nullable = false)
    private Estado estado;

    @Column(name = "\"costoAproximado\"")
    private Double costoAproximado;

    @Column(name = "\"costoReal\"")
    private Double costoReal;

    @Column(name = "\"fechaHoraInicioEstimada\"")
    private LocalDateTime fechaHoraInicioEstimada;

    @Column(name = "\"fechaHoraFinEstimada\"")
    private LocalDateTime fechaHoraFinEstimada;

    @Column(name = "\"fechaHoraInicio\"")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "\"fechaHoraFin\"")
    private LocalDateTime fechaHoraFin;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "\"patenteCamion\"", nullable = true)
    private Camion camion;
}