package org.example.progresssoft.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fxDealsOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("ProgressSoft FX Deals Team");
        contact.setEmail("support@fxdeals.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("FX Deals Warehouse API")
                .version("1.0.0")
                .contact(contact)
                .description("RESTful API for managing Foreign Exchange (FX) deal transactions. " +
                        "This API allows you to import, retrieve, and manage FX deals with comprehensive validation, " +
                        "duplicate detection, and batch processing capabilities.")
                .termsOfService("https://fxdeals.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
