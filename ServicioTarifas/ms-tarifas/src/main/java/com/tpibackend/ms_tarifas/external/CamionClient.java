package com.tpibackend.ms_tarifas.external;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.CamionResumenRemotoDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CamionClient {

    private final RestTemplate restTemplate;
    private final String rutaServiceBaseUrl;

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
            CamionResumenRemotoDTO[] respuesta = restTemplate.getForObject(url, CamionResumenRemotoDTO[].class);
            if (respuesta == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(respuesta);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new IllegalStateException("No se pudieron obtener los camiones", ex);
        }
    }
}
