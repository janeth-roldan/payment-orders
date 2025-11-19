package com.banking.payment.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Configuración para servir el archivo OpenAPI estático.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configura una ruta para servir el archivo OpenAPI estático.
   *
   * @return RouterFunction que maneja la ruta /api/openapi.yaml
   */
  @Bean
  public RouterFunction<ServerResponse> openApiRoute() {
    return RouterFunctions.route()
        .GET("/api/openapi.yaml", request ->
            ServerResponse.ok()
                .contentType(MediaType.parseMediaType("application/x-yaml"))
                .bodyValue(new ClassPathResource("api/openapi.yaml")))
        .build();
  }
}
