package org.tpibackend.mstransportes.entity;

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
@Table(name = "\"TiposTramo\"")
public class TipoTramo {

    @Id
    @Column(name = "\"idTipotramo\"")
    private Integer idTipoTramo;

    @Column(name = "\"nombre\"", nullable = false)
    private String nombre;

    @Column(name = "\"descripcion\"", nullable = false)
    private String descripcion;
}
