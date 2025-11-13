package org.tpibackend.mstransportes.service.osrmstategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.PriorityQueue;

import org.springframework.web.client.RestTemplate;
import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OptimaStrategy implements Strategy {

    private static final double MAX_TRAMO_METROS = 750_000d;

    @Override
    public List<TramoDTO> calcularRuta(
        Ubicacion origen,
        Ubicacion destino,
        List<Deposito> depositos,
        String osrmUrl
    ) {
    Objects.requireNonNull(origen, "La ubicacion de origen no puede ser nula");
    Objects.requireNonNull(destino, "La ubicacion de destino no puede ser nula");
        Objects.requireNonNull(osrmUrl, "La URL de OSRM no puede ser nula");

        List<Ubicacion> nodos = construirNodos(origen, destino, depositos);
        if (nodos.size() < 2) {
            return Collections.emptyList();
        }

        TableResponse tabla = obtenerTablaOsrm(nodos, osrmUrl);
        List<Integer> camino = ejecutarDijkstra(tabla.distancias, 0, nodos.size() - 1);

        return construirTramos(camino, nodos, tabla);
    }

    private List<Ubicacion> construirNodos(
        Ubicacion origen,
        Ubicacion destino,
        List<Deposito> depositos
    ) {
        List<Ubicacion> nodos = new ArrayList<>();
        nodos.add(origen);
        if (depositos != null) {
            for (Deposito deposito : depositos) {
                if (deposito != null && deposito.getUbicacion() != null) {
                    nodos.add(deposito.getUbicacion());
                }
            }
        }
        nodos.add(destino);
        return nodos;
    }

    private TableResponse obtenerTablaOsrm(List<Ubicacion> nodos, String osrmUrl) {
        String baseUrl = osrmUrl.endsWith("/") ? osrmUrl.substring(0, osrmUrl.length() - 1) : osrmUrl;
        String coordenadas = construirCadenaCoordenadas(nodos);
        String url = String.format("%s/table/v1/driving/%s?annotations=distance,duration", baseUrl, coordenadas);

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            @SuppressWarnings("null")
            String respuesta = restTemplate.getForObject(url, String.class);
            if (respuesta == null) {
                throw new IllegalStateException("OSRM devolvió una respuesta vacía");
            }
            JsonNode raiz = mapper.readTree(respuesta);

            if (!"Ok".equals(raiz.path("code").asText())) {
                throw new IllegalStateException("Respuesta no valida de OSRM: " + raiz.path("code").asText());
            }

            double[][] distancias = parsearMatriz(raiz.path("distances"), nodos.size());
            double[][] duraciones = parsearMatriz(raiz.path("durations"), nodos.size());
            return new TableResponse(distancias, duraciones);
        } catch (Exception e) {
            throw new IllegalStateException("Error al consultar OSRM", e);
        }
    }

    private String construirCadenaCoordenadas(List<Ubicacion> nodos) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodos.size(); i++) {
            Ubicacion ubicacion = nodos.get(i);
            if (ubicacion.getLongitud() == null || ubicacion.getLatitud() == null) {
                throw new IllegalArgumentException("Las ubicaciones deben tener latitud y longitud");
            }
            if (i > 0) {
                builder.append(';');
            }
            builder.append(String.format(Locale.US, "%f,%f", ubicacion.getLongitud(), ubicacion.getLatitud()));
        }
        return builder.toString();
    }

    private double[][] parsearMatriz(JsonNode matrizJson, int dimension) {
        double[][] matriz = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            JsonNode fila = matrizJson.get(i);
            for (int j = 0; j < dimension; j++) {
                if (i == j) {
                    matriz[i][j] = 0.0;
                    continue;
                }
                if (fila == null || fila.get(j) == null || fila.get(j).isNull()) {
                    matriz[i][j] = Double.POSITIVE_INFINITY;
                } else {
                    matriz[i][j] = fila.get(j).asDouble(Double.POSITIVE_INFINITY);
                }
            }
        }
        return matriz;
    }

    private List<Integer> ejecutarDijkstra(double[][] distancias, int origen, int destino) {
        int n = distancias.length;
        double[] costo = new double[n];
        int[] previo = new int[n];
        boolean[] visitado = new boolean[n];
        for (int i = 0; i < n; i++) {
            costo[i] = Double.POSITIVE_INFINITY;
            previo[i] = -1;
        }

        costo[origen] = 0.0;
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>((a, b) -> Double.compare(a.distancia, b.distancia));
        cola.offer(new NodoDistancia(origen, 0.0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            int indiceActual = actual.indice;

            if (visitado[indiceActual]) {
                continue;
            }
            visitado[indiceActual] = true;

            if (indiceActual == destino) {
                break;
            }

            for (int vecino = 0; vecino < n; vecino++) {
                double peso = distancias[indiceActual][vecino];
                if (peso > MAX_TRAMO_METROS) {
                    continue;
                }
                if (Double.isInfinite(peso) || Double.isNaN(peso)) {
                    continue;
                }

                double nuevoCosto = costo[indiceActual] + peso;
                if (nuevoCosto < costo[vecino]) {
                    costo[vecino] = nuevoCosto;
                    previo[vecino] = indiceActual;
                    cola.offer(new NodoDistancia(vecino, nuevoCosto));
                }
            }
        }

        if (Double.isInfinite(costo[destino])) {
            throw new IllegalStateException("No se encontro una ruta valida usando OSRM");
        }

        List<Integer> camino = new ArrayList<>();
        for (int nodo = destino; nodo != -1; nodo = previo[nodo]) {
            camino.add(nodo);
        }
        Collections.reverse(camino);
        return camino;
    }

    private List<TramoDTO> construirTramos(
        List<Integer> camino,
        List<Ubicacion> nodos,
        TableResponse tabla
    ) {
        List<TramoDTO> tramos = new ArrayList<>();
        for (int i = 0; i < camino.size() - 1; i++) {
            int origen = camino.get(i);
            int destino = camino.get(i + 1);

            double distancia = tabla.distancias[origen][destino];
            double duracion = tabla.duraciones[origen][destino];
            if (Double.isInfinite(distancia) || Double.isNaN(distancia) || distancia > MAX_TRAMO_METROS) {
                throw new IllegalStateException("Segmento invalido en la ruta calculada");
            }

            TramoDTO tramo = new TramoDTO();
            tramo.setUbicacionOrigen(nodos.get(origen));
            tramo.setUbicacionDestino(nodos.get(destino));
            tramo.setDistancia(distancia);
            tramo.setDuracionEstimada(Double.isFinite(duracion) ? duracion : null);
            tramos.add(tramo);
        }
        return tramos;
    }

    private static class NodoDistancia {
        private final int indice;
        private final double distancia;

        private NodoDistancia(int indice, double distancia) {
            this.indice = indice;
            this.distancia = distancia;
        }
    }

    private static class TableResponse {
        private final double[][] distancias;
        private final double[][] duraciones;

        private TableResponse(double[][] distancias, double[][] duraciones) {
            this.distancias = distancias;
            this.duraciones = duraciones;
        }
    }
}
