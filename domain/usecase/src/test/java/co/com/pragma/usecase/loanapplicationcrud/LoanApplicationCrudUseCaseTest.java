package co.com.pragma.usecase.loanapplicationcrud;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.exception.DomainException;
import co.com.pragma.model.shared.gateway.TransactionalGateway;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.loanapplicationcrud.util.LoanCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class LoanApplicationCrudUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private TransactionalGateway transactionalGateway;

    @InjectMocks
    private LoanApplicationCrudUseCase loanApplicationCrudUseCase;

    private UUID loanTypeId;
    private LoanType loanType;
    private Status pendingStatus;

    @BeforeEach
    void setup() {
        loanTypeId = UUID.randomUUID();
        loanType = LoanType.builder()
                .loanTypeId(loanTypeId)
                .minAmount(BigDecimal.valueOf(1_000))
                .maxAmount(BigDecimal.valueOf(100_000))
                .build();
        pendingStatus = Status.builder()
                .statusId(UUID.randomUUID())
                .name("PENDING")
                .build();

        when(transactionalGateway.execute(any(Mono.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(loanTypeRepository.findById(loanTypeId))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findByName("PENDING"))
                .thenReturn(Mono.just(pendingStatus));
        when(loanApplicationRepository.createLoanApplication(any(LoanApplication.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    @DisplayName("createLoanApplication: success -> persists with pending status and createdAt")
    void createLoanApplication_success() {
        LoanApplication input = LoanCreator.base(loanTypeId);

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                .expectNextMatches(app ->
                        app.getAmount().equals(BigDecimal.valueOf(10_000)) &&
                                app.getTermMonths().equals(12) &&
                                app.getEmail().equals("applicant@mail.com") &&
                                app.getStatusId().equals(pendingStatus.getStatusId()) &&
                                app.getCreatedAt() != null
                )
                .verifyComplete();

        verify(loanTypeRepository).findById(loanTypeId);
        verify(statusRepository).findByName("PENDING");
        verify(loanApplicationRepository).createLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("createLoanApplication: with pre-existing createdAt -> does not override")
    void createLoanApplication_withCreatedAt() {
        LoanApplication input = LoanCreator.withCreatedAt(loanTypeId);

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                .expectNextMatches(app ->
                        app.getCreatedAt().equals(input.getCreatedAt()) // mantiene la fecha original
                )
                .verifyComplete();
    }

    @Nested
    class ValidationErrors {

        @Test
        @DisplayName("null amount -> DomainException REQUIRED_AMOUNT")
        void nullAmount() {
            LoanApplication input = LoanCreator.nullAmount(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("null term -> DomainException REQUIRED_TERM_MONTHS")
        void nullTerm() {
            LoanApplication input = LoanCreator.nullTerm(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("null email -> DomainException REQUIRED_EMAIL")
        void nullEmail() {
            LoanApplication input = LoanCreator.nullEmail(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("invalid email -> DomainException INVALID_EMAIL_FORMAT")
        void invalidEmail() {
            LoanApplication input = LoanCreator.invalidEmail(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("zero or negative term -> DomainException INVALID_TERM_MONTHS")
        void invalidTermMonths() {
            LoanApplication zeroTerm = LoanCreator.zeroTerm(loanTypeId);
            LoanApplication negativeTerm = LoanCreator.negativeTerm(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(zeroTerm))
                    .expectError(DomainException.class)
                    .verify();

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(negativeTerm))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("amount below min -> DomainException INVALID_AMOUNT_RANGE")
        void amountBelowMin() {
            LoanApplication input = LoanCreator.amountBelowMin(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("amount above max -> DomainException INVALID_AMOUNT_RANGE")
        void amountAboveMax() {
            LoanApplication input = LoanCreator.amountAboveMax(loanTypeId);

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }
    }
}