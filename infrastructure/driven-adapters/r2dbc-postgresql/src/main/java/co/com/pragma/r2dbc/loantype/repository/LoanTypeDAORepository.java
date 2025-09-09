package co.com.pragma.r2dbc.loantype.repository;

import co.com.pragma.r2dbc.loantype.data.LoanTypeDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanTypeDAORepository extends R2dbcRepository<LoanTypeDAO, UUID> {

    Mono<LoanTypeDAO> findByName(String loanApplicationTypeName);

}
