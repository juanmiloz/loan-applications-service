package co.com.pragma.r2dbc.loanapplication.repository;

import co.com.pragma.r2dbc.loanapplication.data.LoanApplicationDAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public interface LoanApplicationDAORepository extends R2dbcRepository<LoanApplicationDAO, UUID> {

    Flux<LoanApplicationDAO> findByStatusIdIn(Collection<UUID> statusIds, Pageable pageable);

    Mono<Long> countByStatusIdIn(Collection<UUID> statusIds);

    @Query("""
            SELECT COALESCE(SUM(
                     CASE
                       WHEN lt.interest_rate IS NULL OR la.term_months = 0
                         THEN la.amount / NULLIF(la.term_months, 0)
                       ELSE la.amount * (lt.interest_rate/1200.0)
                            / (1 - POWER(1 + (lt.interest_rate/1200.0), -la.term_months))
                     END
                   ), 0) AS monthly_total
            FROM loan.loan_applications la
            JOIN loan.loan_types     lt ON lt.loan_type_id = la.loan_type_id
            JOIN loan.statuses       s  ON s.status_id     = la.status_id
            WHERE la.email = :email AND s.name = ANY(ARRAY['APPROVED'])
            """)
    Mono<BigDecimal> sumMonthlyApprovedByEmail(String email);

}
