package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI (Swagger) documentation of the Rently application.
 * This class defines the basic information that will be displayed in the Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures an OpenAPI bean for Swagger documentation.
     *
     * @return an OpenAPI object configured with server, contact, license, and API details.
     */
    @Bean
    public OpenAPI openAPI() {
        // Local development server definition
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        // Development team contact information
        Contact contact = new Contact()
                .name("Rently Team")
                .email("rently@example.com");

        // Project license information
        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        // General API information
        Info info = new Info()
                .title("Rently API")
                .version("1.0.0")
                .description("API documentation for Rently application")
                .contact(contact)
                .license(license);

        // Create and return the configured OpenAPI object
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}