package org.tpibackend.mstransportes.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msTransportesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Transportes API")
                        .description("Documentación de la API para el microservicio de transportes")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipo TPI Backend")
                                .email("soporte@tpibackend.org"))
                        .license(new License()
                                .name("Licencia Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio del proyecto")
                        .url("https://example.com/repositorio"));
    }
}
