package co.com.pragma.r2dbc.status;

import co.com.pragma.model.shared.exception.DomainExceptionFactory;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.error.StatusErrorCode;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.r2dbc.status.helper.StatusDAOMapper;
import co.com.pragma.r2dbc.status.repository.StatusDAORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StatusRepositoryAdapter implements StatusRepository {

    private final StatusDAORepository statusDAORepository;
    private final StatusDAOMapper mapper;

    @Override
    public Mono<Status> findByName(String name) {
        return statusDAORepository.findByName(name)
                .switchIfEmpty(Mono.error(
                                DomainExceptionFactory.exceptionOf(StatusErrorCode.STATUS_NOT_FOUND_BY_NAME, name)
                        )
                ).map(mapper::toStatus)
                .doOnSuccess(loanType -> log.info(">>> Returning Status: {}", loanType))
                .doOnError(ex -> log.error(">>> Error finding Status with name {}", name, ex));
    }

    @Override
    public Mono<Status> findById(UUID id) {
        return statusDAORepository.findById(id)
                .switchIfEmpty(Mono.error(
                                DomainExceptionFactory.exceptionOf(StatusErrorCode.STATUS_NOT_FOUND, id)
                        )
                ).map(mapper::toStatus)
                .doOnSuccess(loanType -> log.info(">>> Returning Status: {}", loanType))
                .doOnError(ex -> log.error(">>> Error finding Status with id {}", id, ex));
    }
}
