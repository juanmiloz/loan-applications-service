package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.status.Status;
import reactor.core.publisher.Mono;

public interface LoanDecisionEventPublisher {

    Mono<String> publish(LoanApplication loanApplication, Status status);

}
