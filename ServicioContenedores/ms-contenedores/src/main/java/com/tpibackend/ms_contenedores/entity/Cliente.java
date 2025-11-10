package com.tpibackend.ms_contenedores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @Column(name = "dni", nullable = false)
    private String dni;
    
    @Column(name = "nombre", nullable= false)
    private String nombre;
    
    @Column(name = "apellido", nullable= false)
    private String apellido;
    
    @Column(name = "correo")
    private String correo;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "direccion", nullable= false)
    private String direccion;
}
