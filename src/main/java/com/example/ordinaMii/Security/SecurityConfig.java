package com.example.ordinaMii.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private static final String CLIENT_ID = "OrdinaMii-Backend";
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfig(SecurityExceptionHandler securityExceptionHandler) {
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        // PIATTI / MENU
                        .requestMatchers(HttpMethod.GET, "/dishes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/dishes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/dishes").hasAnyRole("ADMIN", "CUOCO", "CAMERIERE")
                        .requestMatchers(HttpMethod.PUT, "/dishes/**").hasAnyRole("ADMIN", "CUOCO", "CAMERIERE")
                        .requestMatchers(HttpMethod.DELETE, "/dishes/**").hasRole("ADMIN")
                        .requestMatchers("/images/**").permitAll()

                        // TAVOLI
                        .requestMatchers(HttpMethod.GET, "/tables").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tables/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tables").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tables/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tables/**").hasRole("ADMIN")

                        // ORDINI
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("ADMIN", "CAMERIERE", "CUOCO")
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.PUT, "/orders/*/status").hasAnyRole("ADMIN", "CAMERIERE", "CUOCO")
                        .requestMatchers(HttpMethod.PUT, "/orders/*/payment-status").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.PUT, "/orders/**").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")

                        // PRENOTAZIONI
                        .requestMatchers(HttpMethod.GET, "/reservations/**").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.POST, "/reservations").hasAnyRole( "ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.PUT, "/reservations/*/status").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.PUT, "/reservations/**").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.DELETE, "/reservations/**").hasRole("ADMIN")

                        // RICHIESTE ASSISTENZA
                        .requestMatchers(HttpMethod.GET, "/assistance-requests/**").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.POST, "/assistance-requests").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/assistance-requests/*/status").hasAnyRole("ADMIN", "CAMERIERE")

                        // AREA PERSONALE
                        .requestMatchers(HttpMethod.GET, "/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/me/orders").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/me/orders").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/me/reservations").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/me/reservations").hasRole("CLIENTE")

                        // UTENTI
                        .requestMatchers(HttpMethod.GET, "/users/*/orders").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.GET, "/users/*/reservations").hasAnyRole("ADMIN", "CAMERIERE")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")

                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        new KeycloakJwtAuthenticationConverter(CLIENT_ID)
                                )
                        )
                );

        return http.build();
    }
}