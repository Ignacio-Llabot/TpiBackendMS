package org.tpibackend.mstransportes.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.osrmstategies.Strategy;


@Service
public class OsrmService {

    private Strategy strategy;

    private static final Logger log = LoggerFactory.getLogger(OsrmService.class);

    // Usamos 'host.docker.internal' para que el contenedor pueda comunicarse
    // con un servicio (OSRM) que corre en la máquina anfitriona (tu PC).
    // 'localhost' dentro de un contenedor se refiere al propio contenedor.
    private final String osrmUrl = "http://host.docker.internal:5000";

    public List<TramoDTO> calcularTramosDTO(
        Ubicacion origen,
        Ubicacion destino,
        List<Deposito> depositos
    ) {
        log.debug("Invocando estrategia {} para calcular tramos", strategy != null ? strategy.getClass().getSimpleName() : "desconocida");
        return strategy.calcularRuta(origen, destino, depositos, osrmUrl);
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}