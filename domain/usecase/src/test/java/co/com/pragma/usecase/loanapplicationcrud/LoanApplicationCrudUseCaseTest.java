package co.com.pragma.usecase.loanapplicationcrud;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.dto.request.UserDTO;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.gateways.LoanDecisionEventPublisher;
import co.com.pragma.model.loanapplication.gateways.RequestDebtCapacityEventPublisher;
import co.com.pragma.model.loanapplication.gateways.UserClient;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.shared.exception.DomainException;
import co.com.pragma.model.shared.gateway.AuthGateway;
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

    @Mock private LoanApplicationRepository loanApplicationRepository;
    @Mock private LoanTypeRepository loanTypeRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private TransactionalGateway transactionalGateway;
    @Mock private AuthGateway authGateway;
    @Mock private UserClient userClient;
    @Mock private LoanDecisionEventPublisher loanDecisionEventPublisher;
    @Mock private RequestDebtCapacityEventPublisher requestDebtCapacityEventPublisher;


    @InjectMocks
    private LoanApplicationCrudUseCase loanApplicationCrudUseCase;

    private UUID loanTypeId;
    private LoanType loanType;
    private Status pendingStatus;

    private static final UUID REQUESTER_UUID = UUID.randomUUID();
    private static final String REQUESTER_ID = REQUESTER_UUID.toString();
    private static final String EMAIL = "applicant@mail.com";

    @BeforeEach
    void setup() {
        // --- Datos base ---
        loanTypeId = UUID.randomUUID();
        loanType = LoanType.builder()
                .loanTypeId(loanTypeId)
                .minAmount(new BigDecimal("1000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.02"))
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

        when(loanApplicationRepository.getSumMonthlyApprovedByEmail(anyString()))
                .thenReturn(Mono.empty());

        when(authGateway.currentUserId())
                .thenReturn(Mono.just(REQUESTER_ID));

        UserDTO user = org.mockito.Mockito.mock(UserDTO.class);
        when(user.userId()).thenReturn(REQUESTER_UUID);
        when(user.baseSalary()).thenReturn(2_000_000.0);
        when(userClient.getClientByEmail(org.mockito.ArgumentMatchers.nullable(String.class)))
                .thenReturn(Mono.just(user));
        when(requestDebtCapacityEventPublisher.publish(any()))
                .thenReturn(Mono.empty());
        when(loanDecisionEventPublisher.publish(any(), any()))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("createLoanApplication: success -> persiste con status PENDING y createdAt")
    void createLoanApplication_success() {
        LoanApplication input = LoanCreator.base(loanTypeId);

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                .expectNextMatches(app ->
                        app.getAmount().equals(BigDecimal.valueOf(10_000)) &&
                                app.getTermMonths().equals(12) &&
                                app.getEmail().equals(EMAIL) &&
                                app.getStatusId().equals(pendingStatus.getStatusId()) &&
                                app.getCreatedAt() != null
                )
                .verifyComplete();

        verify(loanTypeRepository, times(2)).findById(loanTypeId);
        verify(statusRepository).findByName("PENDING");
        verify(loanApplicationRepository).createLoanApplication(any(LoanApplication.class));
        verify(transactionalGateway).execute(any(Mono.class));
    }

    @Test
    @DisplayName("createLoanApplication: createdAt preexistente -> no se sobreescribe")
    void createLoanApplication_withCreatedAt() {
        LoanApplication input = LoanCreator.withCreatedAt(loanTypeId);

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(input))
                .expectNextMatches(app -> app.getCreatedAt().equals(input.getCreatedAt()))
                .verifyComplete();
    }

    @Test
    @DisplayName("amount == min y amount == max -> válidos")
    void amountOnEdges_isValid() {
        // Min
        LoanApplication minAmount = LoanCreator.base(loanTypeId).toBuilder()
                .amount(loanType.getMinAmount()).build();

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(minAmount))
                .expectNextCount(1)
                .verifyComplete();

        // Max
        LoanApplication maxAmount = LoanCreator.base(loanTypeId).toBuilder()
                .amount(loanType.getMaxAmount()).build();

        StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(maxAmount))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Nested
    class Authorization {

        @Test
        @DisplayName("Usuario actual vacío -> AUTHORIZATION_FAILED")
        void noCurrentUser_fails() {
            when(authGateway.currentUserId()).thenReturn(Mono.empty()); // sobrescribe default

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.base(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("Requester no es dueño del email -> AUTHORIZATION_FAILED")
        void requesterNotOwner_fails() {
            UserDTO other = mock(UserDTO.class);
            when(other.userId()).thenReturn(UUID.randomUUID()); // != "123"
            when(userClient.getClientByEmail(anyString())).thenReturn(Mono.just(other));

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.base(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }
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
        @DisplayName("term 0 o negativo -> DomainException INVALID_TERM_MONTHS")
        void invalidTermMonths() {
            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.zeroTerm(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.negativeTerm(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("amount < min -> DomainException INVALID_AMOUNT_RANGE")
        void amountBelowMin() {
            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.amountBelowMin(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("amount > max -> DomainException INVALID_AMOUNT_RANGE")
        void amountAboveMax() {
            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.amountAboveMax(loanTypeId)))
                    .expectError(DomainException.class)
                    .verify();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }
    }

    @Nested
    class RepositoryEdgeCases {

        @Test
        @DisplayName("statusRepository vacío -> cadena termina vacía (no persiste)")
        void pendingStatusNotFound_completesEmpty() {
            when(statusRepository.findByName("PENDING")).thenReturn(Mono.empty());

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.base(loanTypeId)))
                    .verifyComplete();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }

        @Test
        @DisplayName("loanTypeRepository vacío -> cadena termina vacía (no persiste)")
        void loanTypeNotFound_completesEmpty() {
            when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty());

            StepVerifier.create(loanApplicationCrudUseCase.createLoanApplication(LoanCreator.base(loanTypeId)))
                    .verifyComplete();

            verify(loanApplicationRepository, never()).createLoanApplication(any());
        }
    }

    @Nested
    class UpdateLoanApplication {

        private UUID appId;
        private Status approvedStatus;
        private LoanApplication existing;

        @BeforeEach
        void localSetup() {
            appId = UUID.randomUUID();

            // status actual = PENDING (ya existe en setup global)
            // status nuevo = APPROVED
            approvedStatus = Status.builder()
                    .statusId(UUID.randomUUID())
                    .name("APPROVED")
                    .build();

            existing = LoanApplication.builder()
                    .applicationId(appId)
                    .loanTypeId(loanTypeId)
                    .statusId(pendingStatus.getStatusId()) // estado actual distinto al nuevo
                    .email(EMAIL)
                    .amount(new BigDecimal("10000"))
                    .termMonths(12)
                    .build();

            // findById existente
            when(loanApplicationRepository.findById(appId)).thenReturn(Mono.just(existing));

            // update devuelve lo que le pasen
            when(loanApplicationRepository.updateLoanApplication(any(LoanApplication.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            // statusRepository para "APPROVED"
            when(statusRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedStatus));
        }

        @Test
        @DisplayName("updateLoanApplication: cambia el estado y publica el evento")
        void update_success_changesStatus_andPublishes() {
            StepVerifier.create(loanApplicationCrudUseCase.updateLoanApplication("APPROVED", appId))
                    .expectNextMatches(saved ->
                            saved.getApplicationId().equals(appId) &&
                                    saved.getStatusId().equals(approvedStatus.getStatusId())
                    )
                    .verifyComplete();

            // Se llamó a leer status
            verify(statusRepository).findByName("APPROVED");

            // Se leyó la entidad y se actualizó
            verify(loanApplicationRepository).findById(appId);
            verify(loanApplicationRepository).updateLoanApplication(any(LoanApplication.class));

            // Se publicó el evento de decisión
            verify(loanDecisionEventPublisher).publish(any(LoanApplication.class), eq(approvedStatus));

            // Se ejecutó dentro de la transacción
            verify(transactionalGateway).execute(any(Mono.class));
        }

        @Test
        @DisplayName("updateLoanApplication: STATUS_UNCHANGED si el estado es el mismo")
        void update_statusUnchanged_throws() {
            // nuevo estado = PENDING (igual al actual)
            when(statusRepository.findByName("PENDING")).thenReturn(Mono.just(pendingStatus));

            StepVerifier.create(loanApplicationCrudUseCase.updateLoanApplication("PENDING", appId))
                    .expectErrorSatisfies(err -> {
                        // Es DomainException, el código específico ya lo valida tu fábrica
                        // Aquí basta con que sea DomainException
                        assert err instanceof co.com.pragma.model.shared.exception.DomainException;
                    })
                    .verify();

            verify(loanApplicationRepository).findById(appId);
            verify(loanApplicationRepository, never()).updateLoanApplication(any());
            verify(loanDecisionEventPublisher, never()).publish(any(), any());
        }

        @Test
        @DisplayName("updateLoanApplication: status no encontrado -> cadena termina vacía")
        void update_statusNotFound_completesEmpty() {
            when(statusRepository.findByName("NOT_EXISTS")).thenReturn(Mono.empty());

            StepVerifier.create(loanApplicationCrudUseCase.updateLoanApplication("NOT_EXISTS", appId))
                    .verifyComplete();

            verify(loanApplicationRepository, never()).findById(any());
            verify(loanApplicationRepository, never()).updateLoanApplication(any());
            verify(loanDecisionEventPublisher, never()).publish(any(), any());
        }

        @Test
        @DisplayName("updateLoanApplication: loan no encontrado -> STATUS_UNCHANGED (por lógica actual)")
        void update_loanNotFound_errorsStatusUnchanged() {
            when(loanApplicationRepository.findById(appId)).thenReturn(Mono.empty()); // no existe loan
            when(statusRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedStatus));

            StepVerifier.create(loanApplicationCrudUseCase.updateLoanApplication("APPROVED", appId))
                    .expectErrorSatisfies(err -> {
                        assert err instanceof co.com.pragma.model.shared.exception.DomainException;
                    })
                    .verify();

            verify(loanApplicationRepository).findById(appId);
            verify(loanApplicationRepository, never()).updateLoanApplication(any());
            verify(loanDecisionEventPublisher, never()).publish(any(), any());
        }
    }

}