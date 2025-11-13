package com.tpibackend.ms_contenedores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"Contenedores\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contenedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idContenedor\"", nullable = false)
    private Integer idContenedor;
    
    @Column(name = "peso", nullable = false)
    private Double peso;
    
    @Column(name = "volumen", nullable = false)
    private Double volumen;
    
    @ManyToOne
    @JoinColumn(name = "\"idEstado\"", nullable = false)
    private Estado estado;
}
