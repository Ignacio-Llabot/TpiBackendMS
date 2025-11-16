package com.tpibackend.ms_tarifas.external;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import com.tpibackend.ms_tarifas.external.dto.CamionResumenRemotoDTO;

@Component
public class CamionClient {

    private final RestTemplate restTemplate;
    private final String rutaServiceBaseUrl;
    private static final Logger log = LoggerFactory.getLogger(CamionClient.class);

    public CamionClient(RestTemplate restTemplate, MsServicesProperties properties) {
        this.restTemplate = restTemplate;
        this.rutaServiceBaseUrl = Objects.requireNonNull(properties.getTransportesUrl(), "ms.transportes.url no configurada");
    }

    public List<CamionResumenRemotoDTO> obtenerCamiones() {
        String url = UriComponentsBuilder
            .fromUriString(Objects.requireNonNull(rutaServiceBaseUrl))
            .path("/api/v1/camiones")
            .toUriString();

        try {
            log.info("Consultando camiones remotos en {}", url);
            CamionResumenRemotoDTO[] respuesta = restTemplate.getForObject(url, CamionResumenRemotoDTO[].class);
            if (respuesta == null) {
                log.warn("Respuesta vacia al obtener camiones");
                return Collections.emptyList();
            }
            log.info("Se recuperaron {} camiones remotos", respuesta.length);
            return Arrays.asList(respuesta);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("No se encontraron camiones en el servicio remoto (404)");
                return Collections.emptyList();
            }
            throw ex;
        } catch (RestClientException ex) {
            log.error("Fallo al obtener camiones remotos", ex);
            throw new IllegalStateException("No se pudieron obtener los camiones", ex);
        }
    }
}
