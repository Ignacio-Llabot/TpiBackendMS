package org.tpibackend.mstransportes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"Ubicaciones\"")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idUbicacion\"")
    private Integer idUbicacion;

    @Column(name = "\"direccion\"", nullable = false)
    private String direccion;

    @Column(name = "\"latitud\"", nullable = false)
    private Double latitud;

    @Column(name = "\"longitud\"", nullable = false)
    private Double longitud;
}
