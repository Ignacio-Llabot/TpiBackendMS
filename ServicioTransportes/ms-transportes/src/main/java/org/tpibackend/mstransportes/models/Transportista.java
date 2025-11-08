package org.tpibackend.mstransportes.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"Transportistas\"")
public class Transportista {

    @Id
    @Column(name = "\"idTransportista\"")
    private Integer idTransportista;

    @Column(name = "\"nombre\"", nullable = false)
    private String nombre;

    @Column(name = "\"apellido\"", nullable = false)
    private String apellido;
}
