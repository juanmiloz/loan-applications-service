package co.com.pragma.r2dbc.helper;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.data.LoanApplicationDAO;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface LoanApplicationDAOMapper {

    LoanApplication toLoanApplication(LoanApplicationDAO loanApplicationDAO);

    LoanApplicationDAO toLoanApplicationDAO(LoanApplication loanApplication);

    default Mono<LoanApplication> toLoanApplication(Mono<LoanApplicationDAO> loanApplicationDAO) {
        return loanApplicationDAO.map(this::toLoanApplication);
    }

    default Mono<LoanApplicationDAO> toLoanApplicationDAO(Mono<LoanApplication> loanApplication) {
        return loanApplication.map(this::toLoanApplicationDAO);
    }

    default Flux<LoanApplication> toLoanApplication(Flux<LoanApplicationDAO> loanApplicationDAO) {
        return loanApplicationDAO.map(this::toLoanApplication);
    }

    default Flux<LoanApplicationDAO> toLoanApplicationDAO(Flux<LoanApplication> loanApplication) {
        return loanApplication.map(this::toLoanApplicationDAO);
    }

}
