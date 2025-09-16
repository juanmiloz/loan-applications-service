package co.com.pragma.usecase.loanapplicationcrud;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.error.LoanApplicationErrorCode;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.gateways.LoanDecisionEventPublisher;
import co.com.pragma.model.loanapplication.gateways.UserClient;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.gateway.AuthGateway;
import co.com.pragma.model.shared.gateway.TransactionalGateway;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.loanapplicationcrud.helper.ValidationHelper;
import co.com.pragma.usecase.loanapplicationcrud.contract.LoanApplicationCrudUseCaseContract;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static co.com.pragma.model.shared.exception.DomainExceptionFactory.exceptionOf;

@RequiredArgsConstructor
public class LoanApplicationCrudUseCase implements LoanApplicationCrudUseCaseContract {

    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final String DEFAULT_LOAN_APPLICATION_NAME = "PENDING";
    private final static BigDecimal MAX_BORROWING_PERCENTAGE = new BigDecimal("0.35");
    private final static BigDecimal MONTHS_QUANTITY = new BigDecimal("12");

    private final TransactionalGateway transactionalGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final UserClient userClient;
    private final LoanDecisionEventPublisher loanDecisionEventPublisher;
    private final AuthGateway authGateway;


    @Override
    public Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication) {
        return authGateway.currentUserId().
                switchIfEmpty(Mono.error(exceptionOf(LoanApplicationErrorCode.AUTHORIZATION_FAILED)))
                .flatMap(requesterUserId ->
                        transactionalGateway.execute(
                                ensureOwnerOrDeny(loanApplication.getEmail(), requesterUserId)
                                        .then(validateLoanApplicationFields(loanApplication))
                                        .flatMap(this::attachPendingStatus)
                                        .map(this::stampCreatedAt)
                                        .flatMap(loanApplicationRepository::createLoanApplication)
                        )
                );
    }

    @Override
    public Mono<LoanApplication> updateLoanApplication(String newStatusName, UUID loanToUpdateId) {
        return transactionalGateway.execute(
                statusRepository.findByName(newStatusName)
                        .flatMap(status -> attachNewStatus(loanToUpdateId, status))
        );
    }

    private Mono<Void> calculateDebtCapacity(LoanApplication loanApplication) {
        return Mono.zip(calculateDebtCapacity(loanApplication), calculateNewLoanInstallment(loanApplication))
                .flatMap(tuple -> {

                    return Mono.empty();
                });
    }
    
    private Mono<BigDecimal> calculateAvailableCapacity(LoanApplication loanApplication) {
        Mono<BigDecimal> borrowingCapacity = calculateBorrowingCapacity(loanApplication.getEmail());
        Mono<BigDecimal> currentMonthlyDebt = loanApplicationRepository.getSumMonthlyApprovedByEmail(loanApplication.getEmail()).defaultIfEmpty(BigDecimal.ZERO);

        return Mono.zip(borrowingCapacity, currentMonthlyDebt)
                .map(tuple -> {
                    BigDecimal maxCapacity = tuple.getT1();
                    BigDecimal currentCapacity = tuple.getT2();
                    BigDecimal available = maxCapacity.subtract(currentCapacity);

                    return available;
                });
    }

    private Mono<BigDecimal> calculateBorrowingCapacity(String email) {
        return userClient.getClientByEmail(email)
                .map(user -> BigDecimal.valueOf(user.baseSalary()))
                .map(salary -> salary.multiply(MAX_BORROWING_PERCENTAGE));
    }


    private Mono<BigDecimal> calculateNewLoanInstallment(LoanApplication loanApplication) {
        return loanTypeRepository.findById(loanApplication.getLoanTypeId())
                .map(loanType -> {
                    BigDecimal monthlyInterest = loanType.getInterestRate().divide(MONTHS_QUANTITY);
                    BigDecimal monthsTerm = new BigDecimal(loanApplication.getTermMonths());
                    BigDecimal amount = loanApplication.getAmount();
                    BigDecimal leftEquation = amount.multiply(BigDecimal.ONE.add(monthlyInterest)).multiply(monthsTerm);
                    BigDecimal rightEquation = BigDecimal.ONE.multiply(monthlyInterest).multiply(BigDecimal.ONE.add(monthlyInterest)).multiply(monthsTerm);
                    return leftEquation.subtract(rightEquation);
                });
    }

    private Mono<LoanApplication> attachNewStatus(UUID loanApplicationId, Status status) {
        return loanApplicationRepository.findById(loanApplicationId)
                .filter(la -> !Objects.equals(la.getStatusId(), status.getStatusId()))
                .switchIfEmpty(Mono.error(exceptionOf(LoanApplicationErrorCode.STATUS_UNCHANGED)))
                .map(la -> {
                    la.setStatusId(status.getStatusId());
                    return la;
                }).flatMap(loanApplicationRepository::updateLoanApplication)
                .flatMap(laSaved -> loanDecisionEventPublisher.publish(laSaved, status).thenReturn(laSaved));
    }

    private Mono<Void> ensureOwnerOrDeny(String email, String requesterUserId) {
        return userClient.getClientByEmail(email)
                .map(userDTO -> requesterUserId != null && requesterUserId.equals(String.valueOf(userDTO.userId())))
                .defaultIfEmpty(false)
                .flatMap(matches -> matches
                        ? Mono.<Void>empty()
                        : Mono.error(exceptionOf(LoanApplicationErrorCode.AUTHORIZATION_FAILED)));
    }

    private Mono<LoanApplication> validateLoanApplicationFields(LoanApplication loanApplication) {
        return loanTypeRepository.findById(loanApplication.getLoanTypeId())
                .flatMap(dao -> {
                    ValidationHelper.requireNotNull(loanApplication.getAmount(), LoanApplicationErrorCode.REQUIRED_AMOUNT);
                    ValidationHelper.requireNotNull(loanApplication.getTermMonths(), LoanApplicationErrorCode.REQUIRED_TERM_MONTHS);
                    ValidationHelper.requireNotNull(loanApplication.getEmail(), LoanApplicationErrorCode.REQUIRED_EMAIL);
                    ValidationHelper.requiredRegex(loanApplication.getEmail(), EMAIL_RX, LoanApplicationErrorCode.INVALID_EMAIL_FORMAT);
                    ValidationHelper.requirePositive(loanApplication.getTermMonths(), LoanApplicationErrorCode.INVALID_TERM_MONTHS);
                    validateAmountInRange(loanApplication.getAmount(), dao.getMinAmount(), dao.getMaxAmount());
                    return Mono.just(loanApplication);
                });
    }

    private Mono<LoanApplication> attachPendingStatus(LoanApplication loanApplication) {
        return statusRepository.findByName(DEFAULT_LOAN_APPLICATION_NAME).map(status -> {
            loanApplication.setStatusId(status.getStatusId());
            return loanApplication;
        });
    }

    private LoanApplication stampCreatedAt(LoanApplication loanApplication) {
        if (loanApplication.getCreatedAt() == null) {
            loanApplication.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        }
        return loanApplication;
    }

    private void validateAmountInRange(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
            throw exceptionOf(LoanApplicationErrorCode.INVALID_AMOUNT_RANGE, amount, minAmount, maxAmount);
        }
    }

}
