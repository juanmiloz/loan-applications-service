package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.LoanApplicationDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface LoanApplicationDAORepository extends R2dbcRepository<LoanApplicationDAO, UUID> {
}
