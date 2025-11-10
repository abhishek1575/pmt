//package com.project.pmt.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * Spring Security Configuration
// *
// * Configures:
// * - JWT-based authentication
// * - Role-based authorization
// * - Public and protected endpoints
// * - CORS support
// * - Stateless session management
// */
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final UserDetailsService userDetailsService;
//
//    @Value("${app.jwt.token-prefix:Bearer }")
//    private String tokenPrefix;
//
//    /**
//     * Password encoder bean
//     * Uses BCrypt with strength 10
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * Authentication provider
//     * Configures user details service and password encoder
//     */
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
//
//    /**
//     * Authentication manager bean
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    /**
//     * Security filter chain
//     * Configures all security rules
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .authorizeHttpRequests(authz -> authz
//                        // Public endpoints - no authentication required
//                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
//
//                        // Swagger/OpenAPI endpoints
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/swagger-ui.html",
//                                "/webjars/**",
//                                "/swagger-resources/**"
//                        ).permitAll()
//
//                        // Actuator endpoints
//                        .requestMatchers("/actuator/**").permitAll()
//
//                        // Admin endpoints - require ADMIN or SUPER_ADMIN role
//                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
//
//                        // All other endpoints require authentication
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling()
//                .authenticationEntryPoint((request, response, authException) -> {
//                    response.setStatus(401);
//                    response.setContentType("application/json");
//                    response.getWriter().write(
//                            "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
//                                    "\"status\":401," +
//                                    "\"error\":\"Unauthorized\"," +
//                                    "\"message\":\"" + authException.getMessage() + "\"," +
//                                    "\"path\":\"" + request.getRequestURI() + "\"}"
//                    );
//                })
//                .accessDeniedHandler((request, response, accessDeniedException) -> {
//                    response.setStatus(403);
//                    response.setContentType("application/json");
//                    response.getWriter().write(
//                            "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
//                                    "\"status\":403," +
//                                    "\"error\":\"Forbidden\"," +
//                                    "\"message\":\"Access denied\"," +
//                                    "\"path\":\"" + request.getRequestURI() + "\"}"
//                    );
//                });
//
//        return http.build();
//    }
//}