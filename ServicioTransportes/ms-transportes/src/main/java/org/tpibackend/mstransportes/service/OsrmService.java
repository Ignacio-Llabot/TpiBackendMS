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
import org.tpibackend.mstransportes.dto.TramoDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
public class OsrmService {

    private final DepositoService depositoService;

    public OsrmService(DepositoService depositoService) {
        this.depositoService = depositoService;
    }
    
    private final String osrmUrl = "http://localhost:5000";


   
}