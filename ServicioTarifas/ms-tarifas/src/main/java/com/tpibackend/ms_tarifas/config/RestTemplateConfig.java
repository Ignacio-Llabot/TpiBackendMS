package com.tpibackend.ms_tarifas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.tpibackend.ms_tarifas.logging.LoggingConstants;

@Configuration
public class RestTemplateConfig {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .additionalInterceptors(traceIdInterceptor())
            .build();
    }

    private ClientHttpRequestInterceptor traceIdInterceptor() {
        return (request, body, execution) -> {
            String traceId = MDC.get(LoggingConstants.TRACE_ID_HEADER);
            if (StringUtils.hasText(traceId)) {
                request.getHeaders().set(LoggingConstants.TRACE_ID_HEADER, traceId);
            }
            log.debug("trace-id propagated to {} {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        };
    }
}
