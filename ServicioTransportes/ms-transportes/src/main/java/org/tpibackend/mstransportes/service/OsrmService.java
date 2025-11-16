package org.tpibackend.mstransportes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.service.osrmstategies.Strategy;
import org.tpibackend.mstransportes.dto.TramoDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
public class OsrmService {

    private DepositoService depositoService;

    private Strategy strategy;

    public OsrmService(DepositoService depositoService) {
        this.depositoService = depositoService;
    }
    
    // Usamos 'host.docker.internal' para que el contenedor pueda comunicarse
    // con un servicio (OSRM) que corre en la máquina anfitriona (tu PC).
    // 'localhost' dentro de un contenedor se refiere al propio contenedor.
    private final String osrmUrl = "http://host.docker.internal:5000";

    public List<TramoDTO> calcularTramosDTO(
        Ubicacion origen,
        Ubicacion destino,
        List<Deposito> depositos
    ) {
        return strategy.calcularRuta(origen, destino, depositos, osrmUrl);
    }
}