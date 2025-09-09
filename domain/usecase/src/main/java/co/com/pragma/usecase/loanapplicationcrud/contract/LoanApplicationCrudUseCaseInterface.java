package co.com.pragma.usecase.loanapplicationcrud.contract;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationCrudUseCaseInterface {

    Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication);

}
