package org.tpibackend.mstransportes.service.osrmstategies;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.web.client.RestTemplate;
import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UrgenteStrategy implements Strategy {
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

        validarCoordenadas(origen, "origen");
        validarCoordenadas(destino, "destino");

        if (mismaUbicacion(origen, destino)) {
            TramoDTO tramo = new TramoDTO();
            tramo.setUbicacionOrigen(origen);
            tramo.setUbicacionDestino(destino);
            tramo.setDistancia(0.0);
            tramo.setDuracionEstimada(0.0);
            return Collections.singletonList(tramo);
        }

        RutaResponse ruta = obtenerRutaOsrm(origen, destino, osrmUrl);

        TramoDTO tramo = new TramoDTO();
        tramo.setUbicacionOrigen(origen);
        tramo.setUbicacionDestino(destino);
        tramo.setDistancia(ruta.distancia);
        tramo.setDuracionEstimada(ruta.duracion);

        return Collections.singletonList(tramo);
    }

    private RutaResponse obtenerRutaOsrm(Ubicacion origen, Ubicacion destino, String osrmUrl) {
        String baseUrl = osrmUrl.endsWith("/") ? osrmUrl.substring(0, osrmUrl.length() - 1) : osrmUrl;
        String coordenadas = construirCoordenadas(origen, destino);
        String url = String.format(
            "%s/route/v1/driving/%s?overview=false&alternatives=false&steps=false",
            baseUrl,
            coordenadas
        );

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            @SuppressWarnings("null")
            String respuesta = restTemplate.getForObject(url, String.class);
            if (respuesta == null) {
                throw new IllegalStateException("OSRM devolvio una respuesta vacia");
            }
            JsonNode raiz = mapper.readTree(respuesta);

            if (!"Ok".equals(raiz.path("code").asText())) {
                throw new IllegalStateException("OSRM /route devolvio codigo diferente a Ok: " + raiz);
            }

            JsonNode ruta = raiz.path("routes").path(0);
            if (ruta.isMissingNode()) {
                throw new IllegalStateException("OSRM /route no devolvio rutas para las coordenadas dadas");
            }

            double distancia = ruta.path("distance").asDouble(Double.NaN);
            double duracion = ruta.path("duration").asDouble(Double.NaN);

            if (Double.isNaN(distancia) || Double.isNaN(duracion)) {
                throw new IllegalStateException("OSRM /route devolvio distancia o duracion invalida");
            }

            return new RutaResponse(distancia, duracion);
        } catch (Exception e) {
            throw new IllegalStateException("Error consultando OSRM /route: " + e.getMessage(), e);
        }
    }

    private String construirCoordenadas(Ubicacion origen, Ubicacion destino) {
        return String.format(
            Locale.US,
            "%f,%f;%f,%f",
            origen.getLongitud(),
            origen.getLatitud(),
            destino.getLongitud(),
            destino.getLatitud()
        );
    }

    private void validarCoordenadas(Ubicacion ubicacion, String etiqueta) {
        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            throw new IllegalArgumentException(
                String.format("La ubicacion de %s debe contener latitud y longitud", etiqueta)
            );
        }
    }

    private boolean mismaUbicacion(Ubicacion origen, Ubicacion destino) {
        return Objects.equals(origen.getLatitud(), destino.getLatitud())
            && Objects.equals(origen.getLongitud(), destino.getLongitud());
    }

    private static final class RutaResponse {
        private final double distancia;
        private final double duracion;

        private RutaResponse(double distancia, double duracion) {
            this.distancia = distancia;
            this.duracion = duracion;
        }
    }
}
