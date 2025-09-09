package co.com.pragma.r2dbc.shared;

import co.com.pragma.model.shared.gateway.TransactionalGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransactionalAdapter implements TransactionalGateway {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> execute(Mono<T> action) {
        return transactionalOperator.transactional(action);
    }

    @Override
    public <T> Flux<T> execute(Flux<T> action) {
        return transactionalOperator.transactional(action);
    }

}
