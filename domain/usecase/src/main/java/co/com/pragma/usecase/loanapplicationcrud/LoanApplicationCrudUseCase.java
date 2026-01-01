package co.com.pragma.usecase.loanapplicationcrud;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.dto.request.UserDTO;
import co.com.pragma.model.loanapplication.dto.response.DebtCapacityAutomaticDTO;
import co.com.pragma.model.loanapplication.dto.response.MetricApprovedDTO;
import co.com.pragma.model.loanapplication.error.LoanApplicationErrorCode;
import co.com.pragma.model.loanapplication.gateways.*;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.gateway.AuthGateway;
import co.com.pragma.model.shared.gateway.TransactionalGateway;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.loanapplicationcrud.helper.ValidationHelper;
import co.com.pragma.usecase.loanapplicationcrud.contract.LoanApplicationCrudUseCaseContract;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

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
    private static final String APPROVED = "APPROVED";

    private final TransactionalGateway transactionalGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final UserClient userClient;
    private final LoanDecisionEventPublisher loanDecisionEventPublisher;
    private final RequestDebtCapacityEventPublisher requestDebtCapacityEventPublisher;
    private final MetricApprovedEventPublisher metricApprovedEventPublisher;
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
                                        .flatMap(saved -> buildDebtCapacityAutomaticDTO(saved)
                                                .flatMap(requestDebtCapacityEventPublisher::publish)
                                                .thenReturn(saved)
                                        )
                        )
                );
    }

    @Override
    public Mono<LoanApplication> updateLoanApplication(String newStatusName, UUID loanToUpdateId) {
        return transactionalGateway.execute(
                statusRepository.findByName(newStatusName)
                        .flatMap(status -> attachNewStatus(loanToUpdateId, status))
                        .flatMap(this::emitMetricApprovedEvent)
        );
    }

    private Mono<DebtCapacityAutomaticDTO> buildDebtCapacityAutomaticDTO(LoanApplication loanApplication) {
        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(loanApplication.getLoanTypeId());
        Mono<BigDecimal> currentMonthlyDebt = loanApplicationRepository.getSumMonthlyApprovedByEmail(loanApplication.getEmail()).defaultIfEmpty(BigDecimal.ZERO);
        Mono<UserDTO> userDTOMono = userClient.getClientByEmail(loanApplication.getEmail());

        return Mono.zip(loanTypeMono, currentMonthlyDebt, userDTOMono)
                .map(tuple2 -> new DebtCapacityAutomaticDTO(
                        loanApplication.getApplicationId(),
                        loanApplication.getEmail(),
                        BigDecimal.valueOf(tuple2.getT3().baseSalary()),
                        tuple2.getT2(),
                        tuple2.getT1().getInterestRate(),
                        BigDecimal.valueOf(loanApplication.getTermMonths()),
                        loanApplication.getAmount()
                ));
    }

    private Mono<LoanApplication> emitMetricApprovedEvent(Tuple3<LoanApplication, Status, Status> tuple) {
        LoanApplication la = tuple.getT1();
        String oldName = tuple.getT2().getName();
        String newName = tuple.getT3().getName();

        MetricApprovedDTO metric = null;
        if (!APPROVED.equalsIgnoreCase(oldName) && APPROVED.equalsIgnoreCase(newName)) {
            metric = new MetricApprovedDTO(1, la.getAmount());
        } else if (APPROVED.equalsIgnoreCase(oldName) && !APPROVED.equalsIgnoreCase(newName)) {
            metric = new MetricApprovedDTO(-1, la.getAmount().negate());
        }

        return metric != null
                ? metricApprovedEventPublisher.publish(metric).thenReturn(la)
                : Mono.just(la);
    }

    private Mono<Tuple3<LoanApplication, Status, Status>> attachNewStatus(UUID loanApplicationId, Status status) {
        return loanApplicationRepository.findById(loanApplicationId)
                .filter(la -> !Objects.equals(la.getStatusId(), status.getStatusId()))
                .switchIfEmpty(Mono.error(exceptionOf(LoanApplicationErrorCode.STATUS_UNCHANGED)))
                .flatMap(existing ->
                        statusRepository.findById(existing.getStatusId())
                                .map(oldStatus -> {
                                    existing.setStatusId(status.getStatusId());
                                    return Tuples.of(existing, oldStatus, status);
                                })
                ).flatMap(tuple -> loanApplicationRepository.updateLoanApplication(tuple.getT1())
                        .flatMap(laSaved -> loanDecisionEventPublisher.publish(laSaved, tuple.getT3())
                                .thenReturn(Tuples.of(laSaved, tuple.getT2(), tuple.getT3()))
                        )
                );
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
