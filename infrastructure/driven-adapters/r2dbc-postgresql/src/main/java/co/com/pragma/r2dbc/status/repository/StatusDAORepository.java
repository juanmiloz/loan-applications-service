package co.com.pragma.r2dbc.status.repository;

import co.com.pragma.r2dbc.status.data.StatusDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StatusDAORepository extends R2dbcRepository<StatusDAO, UUID>{

    Mono<StatusDAO> findByName(String name);

}
