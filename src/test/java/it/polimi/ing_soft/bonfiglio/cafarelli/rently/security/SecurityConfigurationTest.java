package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigurationTest {

    @Mock
    private JwtFilter jwtFilter;
    @Mock
    private AuthenticationProvider authenticationProvider;
    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfiguration = new SecurityConfiguration(jwtFilter, authenticationProvider, handlerExceptionResolver);
    }

    @Test
    void roleHierarchy_ShouldDefineCorrectHierarchy() {
        RoleHierarchy hierarchy = securityConfiguration.roleHierarchy();
        assertNotNull(hierarchy);
    }

    @Test
    void methodSecurityExpressionHandler_ShouldUseRoleHierarchy() {
        RoleHierarchy hierarchy = securityConfiguration.roleHierarchy();
        MethodSecurityExpressionHandler handler = securityConfiguration.methodSecurityExpressionHandler(hierarchy);
        assertNotNull(handler);
    }

    @Test
    void corsConfigurationSource_ShouldAllowFrontendAndSwagger() {
        CorsConfigurationSource source = securityConfiguration.corsConfigurationSource();

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/test");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getServletPath()).thenReturn("");

        jakarta.servlet.http.HttpServletMapping mockMapping = mock(jakarta.servlet.http.HttpServletMapping.class);
        when(mockMapping.getMappingMatch()).thenReturn(jakarta.servlet.http.MappingMatch.EXACT);
        when(mockRequest.getHttpServletMapping()).thenReturn(mockMapping);

        CorsConfiguration cors = source.getCorsConfiguration(mockRequest);
        assertNotNull(cors);
        assertTrue(cors.getAllowedMethods().contains("GET"));
        assertTrue(cors.getAllowedOrigins().contains("http://localhost:4200") || cors.getAllowedOrigins().contains("*"));
    }

    @Test
    void securityFilterChain_ShouldBuildCorrectly() throws Exception {
        var http = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class, RETURNS_DEEP_STUBS);
        SecurityFilterChain mockChain = mock(SecurityFilterChain.class);

        when(http.csrf(any())).thenReturn(http);
        when(http.cors(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(http);
        doReturn(mockChain).when(http).build();

        CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
        SecurityFilterChain chain = securityConfiguration.securityFilterChain(http, corsSource);

        assertNotNull(chain);
        verify(http).authenticationProvider(authenticationProvider);
        verify(http).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Test
    void corsConfigurationSource_ShouldRegisterSwaggerAndApiCors() {
        CorsConfigurationSource source = securityConfiguration.corsConfigurationSource();

        // Usa un mock completo invece di una classe anonima
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/swagger-ui/index.html");
        when(mockRequest.getServletPath()).thenReturn("/swagger-ui/index.html");
        when(mockRequest.getContextPath()).thenReturn("");

        // Aggiungi il mock per HttpServletMapping
        jakarta.servlet.http.HttpServletMapping mockMapping = mock(jakarta.servlet.http.HttpServletMapping.class);
        when(mockMapping.getMappingMatch()).thenReturn(jakarta.servlet.http.MappingMatch.EXACT);
        when(mockRequest.getHttpServletMapping()).thenReturn(mockMapping);

        CorsConfiguration swaggerUiCors = source.getCorsConfiguration(mockRequest);

        assertNotNull(swaggerUiCors);
        assertTrue(swaggerUiCors.getAllowedMethods().contains("GET"));
        assertTrue(swaggerUiCors.getAllowedOrigins().contains("*"));
    }
}
