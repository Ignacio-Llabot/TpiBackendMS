package com.tpibackend.ms_tarifas.external;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.SolicitudRemotaDTO;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SolicitudClient {

    private final RestTemplate restTemplate;
    private final String contenedoresServiceBaseUrl;

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
            return restTemplate.getForObject(url, SolicitudRemotaDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new IllegalStateException("No se pudo obtener la solicitud " + solicitudId, ex);
        }
    }
}
