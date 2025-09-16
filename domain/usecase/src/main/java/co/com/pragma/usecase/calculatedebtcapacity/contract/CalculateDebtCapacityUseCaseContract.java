package co.com.pragma.usecase.calculatedebtcapacity.contract;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface CalculateDebtCapacityUseCaseContract {

    Mono<Void> calculateDebtCapacity(LoanApplication loanApplication);

}
