package com.tpibackend.ms_tarifas.external;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.RutaRemotaDTO;

@Component
public class RutaClient {

    private final RestTemplate restTemplate;
    private final String rutaServiceBaseUrl;
    private static final Logger log = LoggerFactory.getLogger(RutaClient.class);

    public RutaClient(RestTemplate restTemplate, MsServicesProperties properties) {
        this.restTemplate = restTemplate;
        this.rutaServiceBaseUrl = Objects.requireNonNull(properties.getTransportesUrl(), "ms.transportes.url no configurada");
    }

    public RutaRemotaDTO obtenerRuta(Integer rutaId) {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(rutaServiceBaseUrl))
            .path("/api/v1/rutas/{idRuta}/detalle")
            .buildAndExpand(rutaId)
            .toUriString();

        try {
            log.info("Consultando ruta {} en servicio remoto", rutaId);
            return restTemplate.getForObject(url, RutaRemotaDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Ruta {} no encontrada en servicio remoto (404)", rutaId);
                return null;
            }
            throw ex;
        } catch (RestClientException ex) {
            log.error("Fallo al obtener la ruta {}", rutaId, ex);
            throw new IllegalStateException("No se pudo obtener la ruta " + rutaId, ex);
        }
    }
}
