package co.com.pragma.api;

import co.com.pragma.api.interfaces.ApplicationHandlerAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(ApplicationHandlerAPI handler) {
        return route(POST("/api/v1/applications"), handler::createLoanApplication)
                .andRoute(GET("/api/v1/applications"), handler::listReviewableLoanApplications);

    }
}
