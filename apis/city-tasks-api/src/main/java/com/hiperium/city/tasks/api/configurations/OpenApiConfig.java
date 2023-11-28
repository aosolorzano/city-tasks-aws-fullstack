package com.hiperium.city.tasks.api.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${info.app.version:1.0.0}")
    private String projectVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("https://dev.hiperium.cloud");
        devServer.setDescription("Development environment for City Tasks API.");

        Contact contact = new Contact();
        contact.setEmail("aosolorzano@example.com");
        contact.setName("Andres Solorzano");
        contact.setUrl("https://aosolorzano.github.io");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
        Info info = new Info()
                .title("City Tasks API")
                .version(this.projectVersion)
                .contact(contact)
                .description("This API exposes endpoints to manage Quartz Jobs for Smart Cities.")
                .termsOfService("https://hiperium.cloud/terms")
                .license(mitLicense);
        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
