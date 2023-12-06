package com.nisolabluap.quickstart.application.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Paul Baloșin",
                        email = "balosin.paul@protonmail.com"
                ),
                description = "A system that helps retailers track and manage their inventory, " +
                        "supplier relationships, and stock levels, with features for handling product bundles and promotions.",
                title = "Retail Inventory Management",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080/"
                )
        }
)
public class OpenApiConfig {

}
