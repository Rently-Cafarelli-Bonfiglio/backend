package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.*;

import static org.mockito.Mockito.*;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
    }

    /**
     * Test per verificare che il metodo addResourceHandlers registri correttamente i percorsi delle immagini e di Swagger.
     * Utilizza i mock per ResourceHandlerRegistry e ResourceHandlerRegistration.
     */
    @Test
    void addResourceHandlers_ShouldRegisterImageAndSwaggerPaths() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration imageHandler = mock(ResourceHandlerRegistration.class);
        ResourceHandlerRegistration swaggerHandler = mock(ResourceHandlerRegistration.class);
        ResourceChainRegistration chainRegistration= mock(ResourceChainRegistration.class);

        doReturn(imageHandler).when(registry).addResourceHandler("/images/**");
        doReturn(swaggerHandler).when(registry).addResourceHandler("/swagger-ui/**");
        doReturn(imageHandler).when(imageHandler).addResourceLocations(anyString());
        doReturn(imageHandler).when(imageHandler).setCachePeriod(anyInt());
        // resourceChain deve ritornare ResourceChainRegistration
        doReturn(chainRegistration).when(imageHandler).resourceChain(anyBoolean());

        doReturn(swaggerHandler).when(swaggerHandler).addResourceLocations(anyString());
        doReturn(chainRegistration).when(swaggerHandler).resourceChain(anyBoolean());

        webConfig.addResourceHandlers(registry);

        verify(registry).addResourceHandler("/images/**");
        verify(registry).addResourceHandler("/swagger-ui/**");
        verify(imageHandler).addResourceLocations(anyString());
        verify(swaggerHandler).addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }

    @Test
    void addCorsMappings_ShouldRegisterApiCors() {
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration corsRegistration = mock(CorsRegistration.class);

        // Configura il mock di CorsRegistration
        doReturn(corsRegistration).when(registry).addMapping("/api/**");
        doReturn(corsRegistration).when(corsRegistration).allowedOrigins("http://localhost:4200");
        doReturn(corsRegistration).when(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE");

        webConfig.addCorsMappings(registry);

        verify(registry).addMapping("/api/**");
        verify(corsRegistration).allowedOrigins("http://localhost:4200");
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}