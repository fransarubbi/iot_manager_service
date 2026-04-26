package com.iot.managerservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de Spring Web MVC enfocada en las reglas de interoperabilidad web.
 * <p>
 * Implementa las políticas de Cross-Origin Resource Sharing (CORS) necesarias para que
 * clientes web modernos alojados en dominios o puertos diferentes puedan consumir la API REST
 * del Manager sin ser bloqueados por los navegadores web.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registra los mapeos y permisos de CORS.
     * <p>
     * Se autoriza el tráfico proveniente de los entornos de desarrollo locales típicos
     * de Node/Vite (puertos 3000 y 5173), permitiendo métodos HTTP estándar y el paso
     * de credenciales/tokens de autorización.
     * </p>
     *
     * @param registry El registro de configuración CORS de Spring.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}