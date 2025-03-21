package com.WalletProject.WalletProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/eureka/**","/**").permitAll() // Allow these without auth
                        .anyRequest().authenticated()) // Require auth for others
                .csrf(AbstractHttpConfigurer::disable); // Disable CSRF (if not using browser forms)

        return http.build();
    }

    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }
}
