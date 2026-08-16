package com.talkfriendly.api.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration @EnableWebSecurity @EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http.csrf(csrf->csrf.disable()).cors(cors->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth->auth
                .requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/actuator/health").permitAll()
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origins) {
        CorsConfiguration config=new CorsConfiguration(); config.setAllowedOrigins(origins); config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); config.setAllowedHeaders(List.of("Authorization","Content-Type")); config.setExposedHeaders(List.of("Authorization")); config.setAllowCredentials(true); config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",config); return source;
    }
}
