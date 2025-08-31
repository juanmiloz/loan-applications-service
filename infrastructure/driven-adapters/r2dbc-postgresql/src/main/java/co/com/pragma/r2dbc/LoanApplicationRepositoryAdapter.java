package co.com.pragma.r2dbc;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.r2dbc.helper.LoanApplicationDAOMapper;
import co.com.pragma.r2dbc.repository.LoanApplicationDAORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final LoanApplicationDAORepository loanApplicationRepository;
    private final LoanApplicationDAOMapper mapper;

    @Override
    public Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication) {
        return Mono.just(loanApplication)
                .map(mapper::toLoanApplicationDAO)
                .doOnNext(dao -> log.info(">>> Saving entity: {}", dao))
                .flatMap(loanApplicationRepository::save)
                .map(mapper::toLoanApplication);
    }
}
