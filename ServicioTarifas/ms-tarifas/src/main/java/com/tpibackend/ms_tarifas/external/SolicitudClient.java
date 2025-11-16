package com.tpibackend.ms_tarifas.external;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.ActualizarCostoEstimadoRequest;
import com.tpibackend.ms_tarifas.external.dto.ActualizarCostoFinalRequest;
import com.tpibackend.ms_tarifas.external.dto.SolicitudRemotaDTO;

@Component
public class SolicitudClient {

    private final RestTemplate restTemplate;
    private final String contenedoresServiceBaseUrl;
    private static final Logger log = LoggerFactory.getLogger(SolicitudClient.class);

    public SolicitudClient(RestTemplate restTemplate, MsServicesProperties properties) {
        this.restTemplate = restTemplate;
        this.contenedoresServiceBaseUrl = Objects.requireNonNull(properties.getContenedoresUrl(), "ms.contenedores.url no configurada");
    }

    public SolicitudRemotaDTO obtenerSolicitud(Integer solicitudId) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(contenedoresServiceBaseUrl))
            .path("/api/v1/solicitudes/{id}")
            .buildAndExpand(solicitudId)
            .toUriString();

        try {
            log.info("Consultando solicitud remota {}", solicitudId);
            return restTemplate.getForObject(url, SolicitudRemotaDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Solicitud remota {} no encontrada (404)", solicitudId);
                return null;
            }
            throw ex;
        } catch (RestClientException ex) {
            log.error("Fallo al obtener la solicitud {}", solicitudId, ex);
            throw new IllegalStateException("No se pudo obtener la solicitud " + solicitudId, ex);
        }
    }

    @SuppressWarnings("null")
    public void actualizarCostoEstimado(Integer solicitudId, Double costoEstimado) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(contenedoresServiceBaseUrl))
            .path("/api/v1/solicitudes/{id}/costo-estimado")
            .buildAndExpand(solicitudId)
            .toUriString();

        ActualizarCostoEstimadoRequest payload = new ActualizarCostoEstimadoRequest(costoEstimado);
        HttpEntity<ActualizarCostoEstimadoRequest> entity = new HttpEntity<>(payload);

        try {
            log.info("Actualizando costo estimado de la solicitud {} a {}", solicitudId, costoEstimado);
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Fallo al actualizar el costo estimado de la solicitud {}", solicitudId, ex);
            throw new IllegalStateException("No se pudo actualizar el costo estimado de la solicitud " + solicitudId, ex);
        }
    }

    @SuppressWarnings("null")
    public void actualizarCostoFinal(Integer solicitudId, Double costoFinal) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(contenedoresServiceBaseUrl))
            .path("/api/v1/solicitudes/{id}/costo-final")
            .buildAndExpand(solicitudId)
            .toUriString();

        ActualizarCostoFinalRequest payload = new ActualizarCostoFinalRequest(costoFinal);
        HttpEntity<ActualizarCostoFinalRequest> entity = new HttpEntity<>(payload);

        try {
            log.info("Actualizando costo final de la solicitud {} a {}", solicitudId, costoFinal);
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Fallo al actualizar el costo final de la solicitud {}", solicitudId, ex);
            throw new IllegalStateException("No se pudo actualizar el costo final de la solicitud " + solicitudId, ex);
        }
    }
}
