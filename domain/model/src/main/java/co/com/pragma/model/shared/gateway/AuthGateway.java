package co.com.pragma.model.shared.gateway;

import reactor.core.publisher.Mono;

public interface AuthGateway {

    Mono<String> currentUserId();

}
