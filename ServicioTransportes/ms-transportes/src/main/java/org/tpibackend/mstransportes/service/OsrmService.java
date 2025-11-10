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


    public double[] calcularDistanciaEntreUbicaciones(Ubicacion origen, Ubicacion destino) {

        double distancia = 0.0;
        double tiempo = 0.0;
        // Construir la URL de OSRM
        String url = String.format("%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                osrmUrl,
                origen.getLongitud(), origen.getLatitud(),
                destino.getLongitud(), destino.getLatitud());

        // Crear RestTemplate para hacer la petición HTTP
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // Hacer GET request a OSRM
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            
            // Verificar que la respuesta sea exitosa
            String code = rootNode.path("code").asText();
            
            if ("Ok".equals(code)) {
                // Navegar: root -> routes (array) -> primer elemento -> distance
                JsonNode routesNode = rootNode.path("routes");
                
                if (routesNode.isArray() && routesNode.size() > 0) {
                    JsonNode firstRoute = routesNode.get(0);
                    distancia = firstRoute.path("distance").asDouble();
                    tiempo = firstRoute.path("duration").asDouble();
                    
                    System.out.println("Distancia calculada: " + distancia + " metros");
                } else {
                    System.err.println("No se encontraron rutas en la respuesta");
                }
            } else {
                System.err.println("Error en OSRM: " + code);
            }
            
        } catch (Exception e) {
            System.err.println("Error al consultar OSRM: " + e.getMessage());
        }

        return new double[] {distancia, tiempo}; // Retornar la distancia calculada en metros y el tiempo en segundos
    }


    public List<TramoDTO> calcularRutaConDepositos(Ubicacion origen, Ubicacion destino) {
        final double MAX_DISTANCIA_SIN_DEPOSITO = 150000.0; // 150 km en metros
        
        // 1. Obtener todos los depósitos disponibles
        List<Deposito> todosDepositos = depositoService.getDepositos();
        List<Ubicacion> ubicacionesDepositos = todosDepositos.stream()
            .map(Deposito::getUbicacion)
            .collect(Collectors.toList());
        
        // 2. Verificar si se necesita pasar por depósitos
        double distanciaDirecta = calcularDistanciaEntreUbicaciones(origen, destino)[0];
        
        if (distanciaDirecta <= MAX_DISTANCIA_SIN_DEPOSITO) {
            // Ruta directa sin depósitos
            return Collections.singletonList(crearTramo(origen, destino));
        }
        
        // 3. Construir matriz de distancias usando OSRM Table Service
        List<Ubicacion> todasUbicaciones = new ArrayList<>();
        todasUbicaciones.add(origen);
        todasUbicaciones.addAll(ubicacionesDepositos);
        todasUbicaciones.add(destino);
        
        double[][] matrizDistancias = obtenerMatrizDistancias(todasUbicaciones);
        
        // 4. Encontrar la ruta óptima usando Dijkstra modificado
        List<Integer> indicesRuta = encontrarRutaOptima(
            matrizDistancias, 
            0, // índice origen
            todasUbicaciones.size() - 1, // índice destino
            MAX_DISTANCIA_SIN_DEPOSITO
        );
        
        // 5. Convertir índices a tramos
        List<TramoDTO> tramos = new ArrayList<>();
        for (int i = 0; i < indicesRuta.size() - 1; i++) {
            Ubicacion ubicOrigen = todasUbicaciones.get(indicesRuta.get(i));
            Ubicacion ubicDestino = todasUbicaciones.get(indicesRuta.get(i + 1));
            tramos.add(crearTramo(ubicOrigen, ubicDestino));
        }
        
        return tramos;
    }

    /**
     * Usa el Table Service de OSRM para obtener todas las distancias entre ubicaciones
     */
    private double[][] obtenerMatrizDistancias(List<Ubicacion> ubicaciones) {
        int n = ubicaciones.size();
        double[][] matriz = new double[n][n];
        
        // Construir coordenadas en formato OSRM: "lon,lat;lon,lat;..."
        String coordinates = ubicaciones.stream()
            .map(u -> String.format("%f,%f", u.getLongitud(), u.getLatitud()))
            .collect(Collectors.joining(";"));
        
        // Llamada al Table Service de OSRM
        String url = String.format("%s/table/v1/driving/%s?annotations=distance,duration",
            osrmUrl, coordinates);
        
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            
            if ("Ok".equals(rootNode.path("code").asText())) {
                JsonNode distancesNode = rootNode.path("distances");
                
                // Parsear matriz de distancias
                for (int i = 0; i < n; i++) {
                    JsonNode row = distancesNode.get(i);
                    for (int j = 0; j < n; j++) {
                        matriz[i][j] = row.get(j).asDouble();
                    }
                }
            } else {
                throw new RuntimeException("Error en OSRM Table: " + rootNode.path("code").asText());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar OSRM Table Service", e);
        }
        
        return matriz;
}

    /**
     * Algoritmo de Dijkstra modificado que respeta la restricción de distancia máxima
     * y minimiza la cantidad de depósitos visitados
     */
    private List<Integer> encontrarRutaOptima(
        double[][] distancias, 
        int inicio, 
        int fin, 
        double maxDistancia
    ) {
        int n = distancias.length;
        
        // Priority queue: (nodo actual, distancia acumulada, cantidad de paradas, ruta)
        PriorityQueue<RutaEstado> cola = new PriorityQueue<>(
            Comparator.comparingInt(RutaEstado::getCantidadParadas)
                    .thenComparingDouble(RutaEstado::getDistanciaTotal)
        );
        
        // Estado inicial
        List<Integer> rutaInicial = new ArrayList<>();
        rutaInicial.add(inicio);
        cola.offer(new RutaEstado(inicio, 0.0, 0, rutaInicial));
        
        // Visitados: Map<nodo, distancia mínima para llegar>
        Map<Integer, Double> visitados = new HashMap<>();
        
        while (!cola.isEmpty()) {
            RutaEstado actual = cola.poll();
            
            // Si llegamos al destino, retornamos la ruta
            if (actual.getNodoActual() == fin) {
                return actual.getRuta();
            }
            
            // Si ya visitamos este nodo con mejor distancia, skip
            if (visitados.containsKey(actual.getNodoActual()) 
                && visitados.get(actual.getNodoActual()) < actual.getDistanciaTotal()) {
                continue;
            }
            
            visitados.put(actual.getNodoActual(), actual.getDistanciaTotal());
            
            // Explorar vecinos
            for (int vecino = 0; vecino < n; vecino++) {
                if (vecino == actual.getNodoActual()) continue;
                
                double distanciaAlVecino = distancias[actual.getNodoActual()][vecino];
                
                // Respetar restricción de distancia máxima (excepto si es el destino final)
                if (distanciaAlVecino > maxDistancia && vecino != fin) {
                    continue;
                }
                
                // Crear nuevo estado
                List<Integer> nuevaRuta = new ArrayList<>(actual.getRuta());
                nuevaRuta.add(vecino);
                
                cola.offer(new RutaEstado(
                    vecino,
                    actual.getDistanciaTotal() + distanciaAlVecino,
                    actual.getCantidadParadas() + 1,
                    nuevaRuta
                ));
            }
        }
        
        throw new RuntimeException("No se encontró ruta válida entre origen y destino");
    }

    /**
     * Crea un TramoDTO con la información de distancia y duración calculada por OSRM
     */
    private TramoDTO crearTramo(Ubicacion origen, Ubicacion destino) {
        // Calcular distancia y duración usando OSRM
        double[] info = calcularDistanciaEntreUbicaciones(origen, destino);
        
        TramoDTO tramo = new TramoDTO();
        tramo.setUbicacionOrigen(origen);
        tramo.setUbicacionDestino(destino);
        tramo.setDistancia(info[0]); // metros
        tramo.setDuracionEstimada(info[1]); // segundos
        
        return tramo;
    }

    @Data
    @AllArgsConstructor
    private static class RutaEstado {
        private int nodoActual;        // ¿Dónde estoy ahora?
        private double distanciaTotal; // ¿Cuántos metros llevo recorridos?
        private int cantidadParadas;   // ¿Cuántos depósitos visité?
        private List<Integer> ruta;    // ¿Qué camino seguí? [índices de ubicaciones]
    }
}