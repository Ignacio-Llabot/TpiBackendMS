package com.tpibackend.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // [cite: 152, 412]
public class GatewayConfig {

    // Inyectamos los valores que definimos en application.properties [cite: 416-421]
    @Value("${app.microservicios.url-contenedores}")
    private String uriContenedores;

    @Value("${app.microservicios.url-transportes}")
    private String uriTransportes;

    @Value("${app.microservicios.url-tarifas}")
    private String uriTarifas;

    @Bean // [cite: 154, 414]
    public RouteLocator configurarRutas(RouteLocatorBuilder builder) {
        
        // Este es el "mapa de rutas" 
        return builder.routes()
            
            // --- Ruteo a ms_contenedores (y solicitudes, etc.) ---
            .route("ms-contenedores-route", p -> p
                // PREDICADO: Si la URL coincide con CUALQUIERA de estos patrones...
                .path("/api/v1/solicitudes/**",
                        "/api/v1/solicitudes", 
                        "/api/v1/clientes",
                        "/api/v1/clientes/**",
                        "/api/v1/contenedores/**",
                        "/api/v1/contenedores") 
                // ... reenvíala a la URI de ese microservicio [cite: 427]
                .uri(uriContenedores)
            )

            // --- Ruteo a ms_transportes ---
            .route("ms-transportes-route", p -> p
                .path("/api/v1/rutas/**",
                        "/api/v1/rutas",
                        "/api/v1/depositos/**", 
                        "/api/v1/depositos",
                        "/api/v1/camiones/**",
                        "/api/v1/camiones",
                        "/api/v1/tramos/**",
                        "/api/v1/tramos")
                .uri(uriTransportes)
            )
            
            // --- Ruteo a ms_tarifas ---
            .route("ms-tarifas-route", p -> p
                .path("/api/v1/tarifas/**")
                .uri(uriTarifas)
            )
            
            .build(); // [cite: 155, 428]
    }
}
