package com.tpibackend.ms_contenedores.dto;

import com.tpibackend.ms_contenedores.entity.Contenedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorUbicacionDTO {
    private Contenedor contenedor;
    private Double latitud;
    private Double longitud;
}
