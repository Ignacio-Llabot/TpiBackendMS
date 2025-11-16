package com.tpibackend.ms_tarifas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms")
public class MsServicesProperties {
    private String transportesUrl;
    private String contenedoresUrl;
}
