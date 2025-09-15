package co.com.pragma.api;

import co.com.pragma.api.interfaces.ApplicationHandlerAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(ApplicationHandlerAPI handler) {
        return nest(path("/api/v1/applications"),
                route()
                        .POST("", accept(MediaType.APPLICATION_JSON).and(contentType(MediaType.APPLICATION_JSON)), handler::createLoanApplication)
                        .GET("", handler::listReviewableLoanApplications)
                        .PUT("/{id}", accept(MediaType.APPLICATION_JSON).and(contentType(MediaType.APPLICATION_JSON)), handler::updateRequestStatus)
                        .build()
        );
    }
}
