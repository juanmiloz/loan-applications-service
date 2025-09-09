package co.com.pragma.api.interfaces;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ApplicationHandlerAPI {

    Mono<ServerResponse> createLoanApplication(ServerRequest request);

    Mono<ServerResponse> listReviewableLoanApplications(ServerRequest request);

}
