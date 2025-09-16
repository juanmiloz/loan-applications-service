package co.com.pragma.api.api;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ApplicationHandlerAPI {

    Mono<ServerResponse> createLoanApplication(ServerRequest request);

    Mono<ServerResponse> listReviewableLoanApplications(ServerRequest request);

    Mono<ServerResponse> updateRequestStatus(ServerRequest request);

    Mono<ServerResponse> calculateDebtCapacity(ServerRequest request);

}
