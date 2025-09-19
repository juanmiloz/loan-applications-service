package co.com.pragma.usecase.loanapplicationcrud.contract;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationCrudUseCaseContract {

    Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication);

    Mono<LoanApplication> updateLoanApplication(String newStatusName, UUID loanToUpdateId);

}
