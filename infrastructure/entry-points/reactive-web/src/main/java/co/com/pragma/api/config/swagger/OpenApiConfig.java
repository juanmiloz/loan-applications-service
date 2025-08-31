package co.com.pragma.api.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Auth Service API")
                .description("Users CRUD (WebFlux Functional Endpoints)")
                .version("v1"));
    }

}