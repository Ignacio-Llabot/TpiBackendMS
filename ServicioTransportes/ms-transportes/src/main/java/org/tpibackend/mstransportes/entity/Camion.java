package org.tpibackend.mstransportes.entity;

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

@Entity
@Table(name = "Camiones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Camion {

    @Id
    @Column(name = "patente", nullable = false)
    private String patente;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "idTransportista")
    private Transportista transportista;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idTipoCamion", nullable = false)
    private TipoCamion tipoCamion;

    @Column(name = "capacidadPeso", nullable = false)
    private Double capacidadPeso;

    @Column(name = "capacidadVolumen", nullable = false)
    private Double capacidadVolumen;

    @Column(name = "disponibilidad", nullable = false)
    private Boolean disponibilidad;
}