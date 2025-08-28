package co.com.pragma.r2dbc.helper;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.data.LoanTypeDAO;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface LoanTypeDAOMapper {

    LoanType toLoanType(LoanTypeDAO loanTypeDAO);

    LoanTypeDAO toLoanTypeDAO(LoanType loanType);

    default Mono<LoanType> toLoanType(Mono<LoanTypeDAO> loanTypeDAO) { return loanTypeDAO.map(this::toLoanType); }

    default Mono<LoanTypeDAO> toLoanTypeDAOMono(Mono<LoanType> loanType) { return loanType.map(this::toLoanTypeDAO); }

    default Flux<LoanType> toLoanTypeFlux(Flux<LoanTypeDAO> loanTypeDAO) { return loanTypeDAO.map(this::toLoanType); }

    default Flux<LoanTypeDAO> toLoanTypeDAOFlux(Flux<LoanType> loanType) { return loanType.map(this::toLoanTypeDAO); }

}
