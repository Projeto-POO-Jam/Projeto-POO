package com.projetopoo.jam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe para linkar a rota upload com a pasta src/main/resources/static/upload/
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/upload/**")
                .addResourceLocations("file:src/main/resources/static/upload/");
    }
}