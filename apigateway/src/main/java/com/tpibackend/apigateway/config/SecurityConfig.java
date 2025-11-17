package com.tpibackend.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // Ya no necesitamos inyectar el issuerUri aquí

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        // --- LIMPIADO ---
        // Quitamos la configuración manual de jwkSetUri.
        // Spring Boot la tomará automáticamente de application.properties.
        // http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwkSetUri(issuerUri + "/protocol/openid-connect/certs")));
        
        http
            .authorizeExchange(exchanges -> exchanges
                // ms-contenedores
                .pathMatchers(HttpMethod.POST, "/api/v1/solicitudes/**").hasAnyRole("CLIENTE", "OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/solicitudes/**").hasAnyRole("CLIENTE", "OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/solicitudes/*").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/solicitudes/*/costo-estimado").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/solicitudes/*/costo-final").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/clientes").hasAnyRole("CLIENTE", "OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/clientes/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/contenedores/*/estado").hasAnyRole("CLIENTE", "OPERADOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/contenedores").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/contenedores/*/estado").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers(HttpMethod.GET, "/api/v1/contenedores/trackingPend").hasRole("OPERADOR")

                // ms-transportes
                .pathMatchers(HttpMethod.GET, "/api/v1/camiones/**").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers(HttpMethod.POST, "/api/v1/camiones").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/camiones/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.DELETE, "/api/v1/camiones/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/depositos/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/depositos").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/depositos/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.DELETE, "/api/v1/depositos/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/rutas/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/rutas/**").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers(HttpMethod.GET, "/api/v1/tramos/**").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tramos/*/costo-aproximado").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tramos/*/costo-real").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tramos/*/*/encamino").hasAnyRole("TRANSPORTISTA", "OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tramos/*/*/finalizado").hasAnyRole("TRANSPORTISTA", "OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tramos/*/*/*").hasRole("OPERADOR")

                // ms-tarifas
                .pathMatchers(HttpMethod.POST, "/api/v1/tarifas/tarifasAproximadas/**").hasAnyRole("OPERADOR", "CLIENTE")
                .pathMatchers(HttpMethod.POST, "/api/v1/tarifas/tarifasReales/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/tarifas/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/tarifas").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/tarifas/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.DELETE, "/api/v1/tarifas/**").hasRole("OPERADOR")

                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                // Le decimos que use JWT y que aplique nuestro "traductor" de roles.
                // Spring usará las propiedades "issuer-uri" y "jwk-set-uri" automáticamente.
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Este Bean es el "traductor" de roles de Keycloak.
     * (Esto estaba perfecto, no se toca).
     */
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return new ReactiveJwtAuthenticationConverterAdapter(jwt -> {
            
            Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
            
            Collection<GrantedAuthority> authorities = realmAccess.get("roles")
                .stream()
                .map(roleName -> "ROLE_" + roleName.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            return new JwtAuthenticationToken(jwt, authorities);
        });
    }
}