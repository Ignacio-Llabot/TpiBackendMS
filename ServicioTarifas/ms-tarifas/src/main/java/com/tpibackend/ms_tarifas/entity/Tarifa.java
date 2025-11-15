package com.tpibackend.ms_tarifas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "\"Tarifas\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {
    
    @Id
    @Column(name = "\"idTarifa\"", nullable = false)
    private Integer idTarifa;

    @Column(name= "\"costoBaseXKm\"", nullable = false)
    private Double costoBaseXKm;

    @Column(name="\"valorLitroCombustible\"", nullable = false)
    private Double valorLitroCombustible;

    @Column(name="\"consumoCombustibleGeneral\"", nullable = false)
    private Double consumoCombustibleGeneral;

    @Column(name="\"idTipoCamion\"", nullable = false)
    private Integer tipoCamion;
}