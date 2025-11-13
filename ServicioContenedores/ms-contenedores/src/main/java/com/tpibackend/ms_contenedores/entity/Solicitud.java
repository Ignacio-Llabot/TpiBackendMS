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
@Table(name = "\"Solicitudes\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idSolicitud\"", nullable = false)
    private Integer idSolicitud;
    
    @ManyToOne
    @JoinColumn(name = "\"idContenedor\"", nullable = false)
    private Contenedor contenedor;
    
    @ManyToOne
    @JoinColumn(name = "\"dniCliente\"", nullable = false)
    private Cliente cliente;
    
    @Column(name = "\"costoEstimado\"")
    private Double costoEstimado;
    
    @Column(name = "\"tiempoEstimado\"", columnDefinition = "interval")
    private Double tiempoEstimado;
    
    @Column(name = "\"costoFinal\"")
    private Double costoFinal;
    
    @Column(name = "\"tiempoReal\"", columnDefinition = "interval")
    private Double tiempoReal;
    
    @ManyToOne
    @JoinColumn(name = "\"idEstado\"", nullable = false)
    private Estado estado;
}
