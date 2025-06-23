package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void openAPI_ShouldReturnConfiguredOpenAPI() {
        OpenAPI openAPI = openApiConfig.openAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Rently API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("API documentation for Rently application", openAPI.getInfo().getDescription());

        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("Rently Team", openAPI.getInfo().getContact().getName());
        assertEquals("rently@example.com", openAPI.getInfo().getContact().getEmail());

        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("MIT License", openAPI.getInfo().getLicense().getName());

        List<Server> servers = openAPI.getServers();
        assertNotNull(servers);
        assertFalse(servers.isEmpty());
        assertEquals("http://localhost:8080", servers.get(0).getUrl());
        assertEquals("Local Development Server", servers.get(0).getDescription());
    }
}
