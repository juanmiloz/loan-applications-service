package co.com.pragma.usecase.reviewableloanapplications;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.dto.request.UserDTO;
import co.com.pragma.model.loanapplication.dto.response.LoanApplicationReviewSummaryDTO;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.gateways.UserClient;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.model.shared.pagination.PageResult;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.reviewableloanapplications.contract.ReviewableLoanApplicationUseCaseContract;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static co.com.pragma.usecase.reviewableloanapplications.constants.LoanApplicationStatuses.REVIEWABLE_NAMES;

@RequiredArgsConstructor
public class ReviewableLoanApplicationsUseCase implements ReviewableLoanApplicationUseCaseContract {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final UserClient userClient;

    @Override
    public Mono<PageResult<LoanApplicationReviewSummaryDTO>> listReviewableLoanApplications(PageQuery pageQuery) {
        return Flux.fromIterable(REVIEWABLE_NAMES)
                .flatMap(statusRepository::findByName)
                .map(Status::getStatusId)
                .collectList()
                .flatMap(statusIds -> loanApplicationRepository.findByStatusIdIn(statusIds, pageQuery))
                .flatMap(page -> {
                    return Flux.fromIterable(page.content())
                            .flatMapSequential(this::buildDTO)
                            .collectList()
                            .map(contentDtos -> PageResult.of(
                                            contentDtos,
                                            page.page(),
                                            page.size(),
                                            page.totalElements()
                                    )
                            );
                });
    }

    private Mono<LoanApplicationReviewSummaryDTO> buildDTO(LoanApplication la) {
        Mono<String> statusName = statusRepository.findById(la.getStatusId()).map(Status::getName);
        Mono<LoanType> loanType = loanTypeRepository.findById(la.getLoanTypeId());
        Mono<UserDTO> client = userClient.getClientByEmail(la.getEmail());
        Mono<BigDecimal> monthlyApproved = loanApplicationRepository.getSumMonthlyApprovedByEmail(la.getEmail());

        return Mono.zip(statusName, loanType, client, monthlyApproved)
                .map(t -> new LoanApplicationReviewSummaryDTO(
                        la.getAmount(),
                        la.getTermMonths(),
                        la.getEmail(),
                        t.getT3().firstName().concat(t.getT3().lastName()),
                        t.getT2().getName(),
                        t.getT2().getInterestRate(),
                        t.getT1(),
                        t.getT3().baseSalary(),
                        t.getT4().doubleValue(),
                        la.getCreatedAt()
                ));

    }

}
