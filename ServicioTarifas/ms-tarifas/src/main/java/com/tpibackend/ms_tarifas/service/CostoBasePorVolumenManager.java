package com.tpibackend.ms_tarifas.service;

import org.springframework.stereotype.Component;

/**
 * Administrador singleton para calcular el costo base por kilómetro según el volumen del contenedor.
 */
@Component
public class CostoBasePorVolumenManager {

    private static final double COSTO_INCREMENTO_POR_METRO_CUBICO = 1;

    public double calcularCostoBasePorKilometro(double volumenMetrosCubicos) {
        if (volumenMetrosCubicos <= 0) {
            return 0.0;
        }
        return volumenMetrosCubicos * COSTO_INCREMENTO_POR_METRO_CUBICO;
    }
}
