package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for web-related settings in the Rently application.
 * This class customizes resource handling and CORS configuration.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {
    /**
     * The location where images are stored on the file system.
     */
    Path imageUploadDir = Paths.get("storage/images").toAbsolutePath();
    String storageLocation = imageUploadDir.toUri().toString();


    /**
     * Configures resource handlers for serving static resources such as images and Swagger UI.
     *
     * @param registry the {@link ResourceHandlerRegistry} to configure resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Handler for serving images from the file system
        registry.addResourceHandler("/images/**")
                .addResourceLocations(storageLocation)
                .setCachePeriod(3600)
                .resourceChain(true);

        // Handler for serving Swagger UI static resources
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for API endpoints.
     *
     * @param registry the {@link CorsRegistry} to configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200") // Frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}