package com.tpibackend.ms_contenedores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"Estados\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    
    @Id
    @Column(name = "\"idEstado\"", nullable = false)
    private Integer idEstado;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
}
