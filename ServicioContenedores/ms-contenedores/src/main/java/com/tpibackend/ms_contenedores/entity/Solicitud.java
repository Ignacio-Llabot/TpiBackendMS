package com.tpibackend.ms_contenedores.entity;

import java.time.Duration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.tpibackend.ms_contenedores.entity.*; 
// Necesario para las relaciones, me estaba tirando error en el service

@Entity
@Table(name = "Solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    
    @Id
    @Column(name = "idSolicitud", nullable = false)
    private Integer idSolicitud;
    
    @ManyToOne
    @JoinColumn(name = "idContenedor", nullable = false)
    private Contenedor contenedor;
    
    @ManyToOne
    @JoinColumn(name = "dniCliente", nullable = false)
    private Cliente cliente;
    
    @Column(name = "costoEstimado")
    private Double costoEstimado;
    
    @Column(name = "tiempoEstimado", columnDefinition = "interval")
    private Duration tiempoEstimado;
    
    @Column(name = "costoFinal")
    private Double costoFinal;
    
    @Column(name = "tiempoReal", columnDefinition = "interval")
    private Duration tiempoReal;
    
    @ManyToOne
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estado;
}
