package co.com.pragma.model.shared.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionalGateway {

    <T> Mono<T> execute(Mono<T> action);
    <T> Flux<T> execute(Flux<T> action);

}
