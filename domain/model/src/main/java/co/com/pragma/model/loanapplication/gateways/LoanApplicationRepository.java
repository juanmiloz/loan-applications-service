package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.model.shared.pagination.PageResult;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public interface LoanApplicationRepository {

    Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication);

    Mono<PageResult<LoanApplication>> findByStatusIdIn(Collection<UUID> statusIds, PageQuery pageQuery);

    Mono<BigDecimal> getSumMonthlyApprovedByEmail(String email);

}
