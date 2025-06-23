package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

/**
 * Security configuration class for the application.
 * It configures security filters, CORS, and role hierarchy.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@AllArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Bean that defines the role hierarchy for the application.
     * This hierarchy allows roles to inherit permissions from other roles.
     *
     * @return a RoleHierarchy instance with the defined hierarchy
     */

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                """
                ROLE_ADMIN > ROLE_MODERATOR
                ROLE_MODERATOR > ROLE_HOST
                ROLE_HOST > ROLE_CLIENT
                """
        );
        return roleHierarchy;
    }

    /**
     * Method security expression handler that uses the role hierarchy.
     *
     * @param roleHierarchy the role hierarchy to be used
     * @return a MethodSecurityExpressionHandler configured with the role hierarchy
     */

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    /**
     * Security filter chain that configures HTTP security for the application.
     * It sets up CORS, CSRF, session management, and endpoint authorization.
     *
     * @param http the HttpSecurity object to configure
     * @param corsConfigurationSource the CORS configuration source
     * @return a SecurityFilterChain configured with the specified settings
     * @throws Exception if an error occurs during configuration
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // Disable CSRF using lambda DSL
                .csrf(csrf -> csrf.disable())
                // Enable CORS with your CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Configure session management as stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(ApiPathUtil.REST_PATH + "/auth/**").permitAll()
                        // Swagger UI paths
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // Protected paths
//                        .requestMatchers(ApiPathUtil.ADMIN_PATH + "/**").hasRole(Role.ADMIN.name())
//                        .requestMatchers(ApiPathUtil.MODERATOR_PATH + "/**").hasRole(Role.MODERATOR.name())
//                        .requestMatchers(ApiPathUtil.HOST_PATH + "/**").hasRole(Role.HOST.name())
//                        .requestMatchers(ApiPathUtil.CLIENT_PATH + "/**").hasRole(Role.CLIENT.name())
                        .anyRequest().authenticated()
                )
                // Configure exception handling using the injected HandlerExceptionResolver
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                handlerExceptionResolver.resolveException(request, response, null, authException)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                handlerExceptionResolver.resolveException(request, response, null, accessDeniedException)
                        )
                )
                // Set the authentication provider
                .authenticationProvider(authenticationProvider)
                // Add the injected JwtFilter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean that provides CORS configuration for the application.
     * It allows requests from specific origins and methods, and sets allowed headers.
     *
     * @return a CorsConfigurationSource configured with the specified settings
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ApiPathUtil.BASE_PATH + "/**", config);

        // Add CORS configuration for Swagger UI
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOrigins(List.of("*"));
        swaggerConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        swaggerConfig.setAllowedHeaders(List.of("*"));

        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);
        source.registerCorsConfiguration("/api-docs/**", swaggerConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);

        return source;
    }
}
