package co.com.pragma.api;

import co.com.pragma.api.data.request.CreateLoanApplicationDTO;
import co.com.pragma.api.interfaces.ApplicationHandlerAPI;
import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.usecase.loanapplicationcrud.contract.LoanApplicationCrudUseCaseInterface;
import co.com.pragma.usecase.reviewableloanapplications.contract.ReviewableLoanApplicationUseCaseContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationHandler implements ApplicationHandlerAPI {

    private final LoanApplicationCrudUseCaseInterface useCase;
    private final ReviewableLoanApplicationUseCaseContract reviewableUseCase;
    private final LoanApplicationMapper mapper;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Override
    public Mono<ServerResponse> createLoanApplication(ServerRequest request) {
        return request.bodyToMono(CreateLoanApplicationDTO.class)
                .doOnSubscribe(s -> log.debug("createLoanApplication:reading-body"))
                .doOnNext(dto -> log.debug("createLoanApplication:payload-received dto=[masked]"))
                .map(mapper::toEntity)
                .doOnNext(entity -> log.debug("createLoanApplication:mapped-to-entity entityClass={}", entity.getClass().getSimpleName()))
                .flatMap(useCase::createLoanApplication)
                .doOnNext(created -> log.info("createLoanApplication:usecase-success loanApplicationId={}", created.getApplicationId()))
                .map(mapper::toDTO)
                .flatMap(res -> ServerResponse.created(URI.create("/api/v1/loan-application/" + res.applicationId())).bodyValue(res))
                .doOnError(ex -> log.error("createLoanApplication:error msg={}", ex.getMessage(), ex));
    }

    @Override
    public Mono<ServerResponse> listReviewableLoanApplications(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Math.min(Math.max(Integer.parseInt(request.queryParam("size").orElse("20")), 1), 200);
        PageQuery pageQuery = new PageQuery(page, size);

        return reviewableUseCase.listReviewableLoanApplications(pageQuery)
                .flatMap(res -> ServerResponse.ok().bodyValue(res));
    }
}