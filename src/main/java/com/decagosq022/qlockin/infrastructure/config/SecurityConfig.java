package com.decagosq022.qlockin.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        http.csrf(CsrfConfigurer:: disable)
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(antMatcher(HttpMethod.POST, "/api/auth/**"),
                                        antMatcher(HttpMethod.POST, "/api/auth/forgot-password"),
                                        antMatcher(HttpMethod.GET, "/api/auth/confirm"),
                                        antMatcher(HttpMethod.POST, "/api/auth/reset"),
                                        antMatcher(HttpMethod.GET, "/api/auth/confirm-forget-password-token"),
                                        antMatcher(HttpMethod.GET, "/api/auth/confirm"),
                                        antMatcher(HttpMethod.GET, "/swagger-ui.html"),
                                        antMatcher(HttpMethod.GET, "/swagger-ui/**"),
                                        antMatcher(HttpMethod.GET, "/v3/api-docs/**"),
                                        antMatcher(HttpMethod.GET, "/swagger-resources/**"),
                                        antMatcher(HttpMethod.GET, "/api/auth/confirm")
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()

                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        http.authenticationProvider(authenticationProvider);

        http.cors(customizer -> customizer.configurationSource(corsConfigurationSource()));

        return http.build();

    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Set your allowed origins here
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Set your allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // Set your allowed headers (e.g., Content-Type, Authorization)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all paths

        return source;
    }



}
