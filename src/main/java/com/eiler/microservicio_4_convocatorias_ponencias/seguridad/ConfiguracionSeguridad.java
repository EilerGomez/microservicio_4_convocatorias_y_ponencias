/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.seguridad;

/**
 *
 * @author eiler
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // habilita @PreAuthorize en los controladores
public class ConfiguracionSeguridad {

    private final FiltroAutenticacionGateway filtroGateway;

    public ConfiguracionSeguridad(FiltroAutenticacionGateway filtroGateway) {
        this.filtroGateway = filtroGateway;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(401, "No autenticado"))
            )
            .authorizeHttpRequests(auth -> auth
                // Swagger libre
                .requestMatchers(
                    "/swagger-ui",
                    "/swagger-ui/**",
                    "/v1/api-docs",
                    "/v1/api-docs/**",
                    "/swagger-ui.html",
                    "/webjars/**"
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            // Agrega el filtro antes del filtro estandar de Spring
            .addFilterBefore(filtroGateway,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
