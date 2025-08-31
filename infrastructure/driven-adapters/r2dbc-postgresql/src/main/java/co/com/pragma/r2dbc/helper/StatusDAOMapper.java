package co.com.pragma.r2dbc.helper;

import co.com.pragma.model.status.Status;
import co.com.pragma.r2dbc.data.StatusDAO;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface StatusDAOMapper {

    Status toStatus(StatusDAO statusDAO);

    StatusDAO toStatusDAO(Status status);

    default Mono<Status> toStatus(Mono<StatusDAO> statusDAO) { return statusDAO.map(this::toStatus); }

    default Mono<StatusDAO> toStatusDAO(Mono<Status> status) { return status.map(this::toStatusDAO); }

    default Flux<Status> toStatus(Flux<StatusDAO> statusDAO) { return statusDAO.map(this::toStatus); }

    default Flux<StatusDAO> toStatusDAO(Flux<Status> status) { return status.map(this::toStatusDAO); }

}
