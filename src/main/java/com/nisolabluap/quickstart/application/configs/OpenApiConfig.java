package com.nisolabluap.quickstart.application.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Retail Inventory System",
                contact = @Contact(
                        name = "Paul Baloșin",
                        email = "balosin.paul@protonmail.com",
                        url = "https://github.com/Nisolabluap/retail-inventory-management"
                ),
                description = "The Retail Inventory System is a comprehensive software solution designed to streamline and optimize the management of inventory in a retail environment." +
                        "This system facilitates efficient handling of various aspects, including Item creation, customer management, and order processing.",

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
