package co.com.pragma.r2dbc.loanapplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.model.shared.pagination.PageResult;
import co.com.pragma.r2dbc.loanapplication.data.LoanApplicationDAO;
import co.com.pragma.r2dbc.loanapplication.helper.LoanApplicationDAOMapper;
import co.com.pragma.r2dbc.loanapplication.repository.LoanApplicationDAORepository;
import co.com.pragma.r2dbc.shared.helper.PageableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final LoanApplicationDAORepository repository;
    private final LoanApplicationDAOMapper mapper;
    private final PageableMapper pageableMapper;

    @Override
    public Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication) {
        return Mono.just(loanApplication)
                .map(mapper::toLoanApplicationDAO)
                .doOnNext(dao -> log.info(">>> Saving entity: {}", dao))
                .flatMap(repository::save)
                .map(mapper::toLoanApplication);
    }

    @Override
    public Mono<PageResult<LoanApplication>> findByStatusIdIn(Collection<UUID> statusIds, PageQuery pageable) {
        Pageable pageableSpring = pageableMapper.toPageable(pageable);
        int page = Math.max(0, pageableSpring.getPageNumber());
        int size = Math.max(1, pageableSpring.getPageSize());

        Mono<List<LoanApplication>> dataMono = repository.findByStatusIdIn(statusIds, pageableSpring).map(mapper::toLoanApplication).collectList();
        Mono<Long> totalMono = repository.countByStatusIdIn(statusIds);


        return Mono.zip(dataMono, totalMono)
                .map(t -> PageResult.of(t.getT1(), page, size, t.getT2()));
    }

    @Override
    public Mono<BigDecimal> getSumMonthlyApprovedByEmail(String email) {
        return repository.sumMonthlyApprovedByEmail(email);
    }
}
