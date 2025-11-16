package com.tpibackend.ms_tarifas.external;

import com.tpibackend.ms_tarifas.config.MsServicesProperties;
import com.tpibackend.ms_tarifas.external.dto.RutaRemotaDTO;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RutaClient {

    private final RestTemplate restTemplate;
    private final String rutaServiceBaseUrl;

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
            return restTemplate.getForObject(url, RutaRemotaDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new IllegalStateException("No se pudo obtener la ruta " + rutaId, ex);
        }
    }
}
