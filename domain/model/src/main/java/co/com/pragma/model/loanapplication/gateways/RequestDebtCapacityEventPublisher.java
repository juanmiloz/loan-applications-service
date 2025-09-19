package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.dto.response.DebtCapacityAutomaticDTO;
import reactor.core.publisher.Mono;

public interface RequestDebtCapacityEventPublisher {

    Mono<String> publish(DebtCapacityAutomaticDTO debtCapacityAutomaticDTO);


}
