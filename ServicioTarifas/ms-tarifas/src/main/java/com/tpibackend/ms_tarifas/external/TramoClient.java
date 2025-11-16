package com.tpibackend.ms_tarifas.external;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.ActualizarCostoRealTramoRequest;
import com.tpibackend.ms_tarifas.external.dto.ActualizarCostoTramoRequest;

@Component
public class TramoClient {

    private final RestTemplate restTemplate;
    private final String transportesServiceBaseUrl;
    private static final Logger log = LoggerFactory.getLogger(TramoClient.class);

    public TramoClient(RestTemplate restTemplate, MsServicesProperties properties) {
        this.restTemplate = restTemplate;
        this.transportesServiceBaseUrl = Objects.requireNonNull(properties.getTransportesUrl(), "ms.transportes.url no configurada");
    }

    public void actualizarCostoAproximado(Integer tramoId, Double incrementoCosto) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(transportesServiceBaseUrl))
            .path("/api/v1/tramos/{tramoId}/costo-aproximado")
            .buildAndExpand(tramoId)
            .toUriString();

        ActualizarCostoTramoRequest payload = new ActualizarCostoTramoRequest(incrementoCosto);
        HttpEntity<ActualizarCostoTramoRequest> entity = new HttpEntity<>(payload);

        try {
            log.info("Actualizando costo aproximado del tramo {} con incremento {}", tramoId, incrementoCosto);
            @SuppressWarnings("null")
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Respuesta inesperada {} al actualizar costo aproximado del tramo {}", response.getStatusCode(), tramoId);
                throw new IllegalStateException("No se pudo actualizar el costo aproximado del tramo " + tramoId);
            }
        } catch (RestClientException ex) {
            log.error("Fallo al actualizar el costo aproximado del tramo {}", tramoId, ex);
            throw new IllegalStateException("Error al actualizar el costo aproximado del tramo " + tramoId, ex);
        }
    }

    public void actualizarCostoReal(Integer tramoId, Double costoReal) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(transportesServiceBaseUrl))
            .path("/api/v1/tramos/{tramoId}/costo-real")
            .buildAndExpand(tramoId)
            .toUriString();

        ActualizarCostoRealTramoRequest payload = new ActualizarCostoRealTramoRequest(costoReal);
        HttpEntity<ActualizarCostoRealTramoRequest> entity = new HttpEntity<>(payload);

        try {
            log.info("Actualizando costo real del tramo {} a {}", tramoId, costoReal);
            @SuppressWarnings("null")
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Respuesta inesperada {} al actualizar costo real del tramo {}", response.getStatusCode(), tramoId);
                throw new IllegalStateException("No se pudo actualizar el costo real del tramo " + tramoId);
            }
        } catch (RestClientException ex) {
            log.error("Fallo al actualizar el costo real del tramo {}", tramoId, ex);
            throw new IllegalStateException("Error al actualizar el costo real del tramo " + tramoId, ex);
        }
    }
}
