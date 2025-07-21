package com.utpsistemas.distribuidoraavesservice.auth.config;

import com.utpsistemas.distribuidoraavesservice.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
//                                           CustomAccessDeniedHandler customAccessDeniedHandler,
//                                           CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/aves-service/auth/login",
                                "/error"
                        ).permitAll()
//                        .requestMatchers("/aves-service/clientes/**").hasRole("Distribuidor")
//                        .requestMatchers("/aves-service/vendedor/**").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/aves-service/vendedor/**").hasRole("Vendedor")
//                        .requestMatchers("/aves-service/cobrador/**").hasRole("Cobrador")
                        .anyRequest().authenticated()
                )
//                .exceptionHandling(exception -> exception
////                        .accessDeniedHandler(customAccessDeniedHandler)
////                        .authenticationEntryPoint(customAuthenticationEntryPoint)
//                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
