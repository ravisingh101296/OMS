package com.example.order_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf->csrf.disable())
                .formLogin(form->form.disable())
                .httpBasic(basic->basic.disable())
                .sessionManagement(sesson->sesson
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})
                )
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers( "/api/auth/register",
                                "/api/auth/login").permitAll()
                        .requestMatchers("/api/orders/**").authenticated()
                        .anyRequest().authenticated()
                );


        return http.build();
    }
}
