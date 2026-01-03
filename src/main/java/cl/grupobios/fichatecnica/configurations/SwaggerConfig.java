package cl.grupobios.fichatecnica.configurations;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Ficha Técnica API",
        version = "1.0",
        description = "API para la gestión de fichas técnicas de productos.",
        contact = @Contact(
            name = "Esteban Hernán Lobos Canales",
            email = "esteban.hernan.lobos@gmail.com"
        )
    ),
    servers = {
        @Server(
            description = "Servidor Local",
            url = "http://localhost:8080"
        ),
    }
)
public class SwaggerConfig {
    
}
