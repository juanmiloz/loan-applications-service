package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.LoanTypeDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface LoanTypeDAORepository extends R2dbcRepository<LoanTypeDAO, UUID> {
}
