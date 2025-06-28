package com.projetopoo.jam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe para configurar o OpenAPI
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JAM API")
                        .version("v1.0")
                        .description("API para a plataforma de Game Jams. Permite criar e gerenciar Jams, submeter jogos, votar e comentar."));
    }
}