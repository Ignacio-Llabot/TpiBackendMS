package org.tpibackend.mstransportes.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"Rutas\"")
public class Ruta {

    @Id
    @Column(name = "\"idRuta\"", nullable = false)
    private Integer idRuta;

    @Column(name = "\"cantidadTramos\"", nullable = false)
    private Integer cantidadTramos;

    @Column(name = "\"cantidadDepositos\"", nullable = false)
    private Integer cantidadDepositos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUbicacionInicial\"")
    private Ubicacion ubicacionInicial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUbicacionFinal\"")
    private Ubicacion ubicacionFinal;

    @Column(name = "\"idSolicitud\"", nullable = false)
    private Integer idSolicitud;
}
