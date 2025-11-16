package org.tpibackend.mstransportes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.tpibackend.mstransportes.logging.LoggingConstants;

@Configuration
public class RestTemplateConfig {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(traceIdInterceptor());
        return restTemplate;
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
