package co.com.pragma.usecase.reviewableloanapplications.contract;

import co.com.pragma.model.loanapplication.dto.response.LoanApplicationReviewSummaryDTO;
import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.model.shared.pagination.PageResult;
import reactor.core.publisher.Mono;

public interface ReviewableLoanApplicationUseCaseContract {

    Mono<PageResult<LoanApplicationReviewSummaryDTO>> listReviewableLoanApplications(PageQuery pageQuery);

}
