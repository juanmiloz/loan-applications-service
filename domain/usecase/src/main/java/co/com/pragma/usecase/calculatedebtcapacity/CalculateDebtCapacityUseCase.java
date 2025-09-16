package co.com.pragma.usecase.calculatedebtcapacity;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.gateways.UserClient;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.usecase.calculatedebtcapacity.contract.CalculateDebtCapacityUseCaseContract;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CalculateDebtCapacityUseCase implements CalculateDebtCapacityUseCaseContract {

    private final static BigDecimal MAX_BORROWING_PERCENTAGE = new BigDecimal("0.35");
    private final static BigDecimal MONTHS_QUANTITY = new BigDecimal("12");

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserClient userClient;

    @Override
    public Mono<Void> calculateDebtCapacity(LoanApplication loanApplication) {
        return Mono.empty();
    }
}
