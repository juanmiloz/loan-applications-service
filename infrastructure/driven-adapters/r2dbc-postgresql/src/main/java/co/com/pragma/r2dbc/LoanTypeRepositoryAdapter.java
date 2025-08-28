package co.com.pragma.r2dbc;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.error.LoanTypeErrorCode;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.exception.DomainExceptionFactory;
import co.com.pragma.r2dbc.helper.LoanTypeDAOMapper;
import co.com.pragma.r2dbc.repository.LoanTypeDAORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LoanTypeRepositoryAdapter implements LoanTypeRepository {

    private final LoanTypeDAORepository loanTypeRepository;
    private final LoanTypeDAOMapper mapper;

    @Override
    public Mono<LoanType> findById(UUID id) {
        return loanTypeRepository.findById(id)
                .doOnNext(dao -> log.info(">>> Found LoanTypeDAO: {}", dao))
                .switchIfEmpty(Mono.error(
                        DomainExceptionFactory.exceptionOf(LoanTypeErrorCode.LOAN_TYPE_NOT_FOUND, id))
                ).map(mapper::toLoanType)
                .doOnSuccess(loanType -> log.info(">>> Returning LoanType: {}", loanType))
                .doOnError(ex -> log.error(">>> Error finding LoanType with id {}", id, ex));
    }
}
