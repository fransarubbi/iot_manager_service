package com.iot.managerservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Configuración central de seguridad de la aplicación basada en Spring Security.
 * <p>
 * Define las políticas de firewall a nivel de aplicación, estableciendo qué endpoints
 * son públicos y cuáles requieren autorización. Está diseñada para soportar un entorno
 * mixto: tráfico gRPC libre y tráfico REST protegido por tokens JWT para interfaces administrativas.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Construye y configura la cadena de filtros de seguridad HTTP.
     * <ul>
     * <li>Deshabilita CSRF al ser una API Stateless.</li>
     * <li>Permite las solicitudes preflight (OPTIONS) para habilitar peticiones CORS de navegadores.</li>
     * <li>Permite el acceso libre a los canales de comunicación gRPC ({@code /api/grpc/**}).</li>
     * <li>Asegura que cualquier consulta a los endpoints de gestión ({@code /api/**}) contenga un Bearer Token válido.</li>
     * </ul>
     *
     * @param http El constructor de seguridad HTTP proveído por Spring.
     * @return La cadena de filtros configurada lista para ser registrada.
     * @throws Exception Si ocurre un error de configuración interna.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/grpc/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }
}