package co.com.pragma.sqs.listener;

import co.com.pragma.sqs.listener.dto.LoanDecisionMessage;
import co.com.pragma.usecase.loanapplicationcrud.LoanApplicationCrudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper mapper;
    private final LoanApplicationCrudUseCase loanApplicationCrudUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> mapper.readValue(message.body(), LoanDecisionMessage.class))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(msg -> log.info("Entity received {}", msg))
                .flatMap(loanDecisionMessage -> {
                    UUID id = UUID.fromString(loanDecisionMessage.loanApplicationId());
                    String decision = loanDecisionMessage.decision();
                    return loanApplicationCrudUseCase.updateLoanApplication(decision, id);
                }).then();
    }
}
