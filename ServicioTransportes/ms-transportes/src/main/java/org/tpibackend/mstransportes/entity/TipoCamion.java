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
@Table(name = "\"TiposCamion\"")
public class TipoCamion {

    @Id
    @Column(name = "\"idTipoCamion\"")
    private Integer idTipoCamion;

    @Column(name = "\"nombre\"", nullable = false)
    private String nombre;
}
