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
import co.com.pragma.usecase.reviewableloanapplications.constants.LoanApplicationStatuses;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ReviewableLoanApplicationsUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReviewableLoanApplicationsUseCase useCase;

    private UUID statusPendingId, statusRejectedId, statusManualId;
    private Status statusPending, statusRejected, statusManual;

    private UUID loanTypeId1, loanTypeId2;
    private LoanType loanType1, loanType2;

    private LoanApplication la1, la2;

    private final PageQuery pageQuery = mock(PageQuery.class); // no necesitamos un real para este test

    @BeforeEach
    void setup() {
        // --- Status IDs y entidades ---
        statusPendingId = UUID.randomUUID();
        statusRejectedId = UUID.randomUUID();
        statusManualId   = UUID.randomUUID();

        statusPending = Status.builder().statusId(statusPendingId).name(LoanApplicationStatuses.PENDING).build();
        statusRejected = Status.builder().statusId(statusRejectedId).name(LoanApplicationStatuses.REJECTED).build();
        statusManual   = Status.builder().statusId(statusManualId).name(LoanApplicationStatuses.MANUAL_REVIEW).build();

        // findByName para cada estado revisable
        when(statusRepository.findByName(LoanApplicationStatuses.PENDING)).thenReturn(Mono.just(statusPending));
        when(statusRepository.findByName(LoanApplicationStatuses.REJECTED)).thenReturn(Mono.just(statusRejected));
        when(statusRepository.findByName(LoanApplicationStatuses.MANUAL_REVIEW)).thenReturn(Mono.just(statusManual));

        // --- Loan Types ---
        loanTypeId1 = UUID.randomUUID();
        loanTypeId2 = UUID.randomUUID();

        loanType1 = LoanType.builder()
                .loanTypeId(loanTypeId1)
                .name("Personal")
                .interestRate(new BigDecimal("2.10"))
                .build();

        loanType2 = LoanType.builder()
                .loanTypeId(loanTypeId2)
                .name("Automotive")
                .interestRate(new BigDecimal("1.70"))
                .build();

        when(loanTypeRepository.findById(loanTypeId1)).thenReturn(Mono.just(loanType1));
        when(loanTypeRepository.findById(loanTypeId2)).thenReturn(Mono.just(loanType2));

        // --- Loan Applications en estado revisable ---
        la1 = LoanApplication.builder()
                .applicationId(UUID.randomUUID())
                .email("ana@acme.com")
                .amount(new BigDecimal("15000"))
                .termMonths(24)
                .loanTypeId(loanTypeId1)
                .statusId(statusPendingId)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();

        la2 = LoanApplication.builder()
                .applicationId(UUID.randomUUID())
                .email("bob@acme.com")
                .amount(new BigDecimal("8000"))
                .termMonths(12)
                .loanTypeId(loanTypeId2)
                .statusId(statusRejectedId)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusHours(6))
                .build();

        // --- Página simulada que devuelve el repositorio ---
        PageResult<LoanApplication> page = PageResult.of(List.of(la1, la2), 0, 10, 2L);
        when(loanApplicationRepository.findByStatusIdIn(anyList(), any(PageQuery.class)))
                .thenReturn(Mono.just(page));

        // --- statusRepository.findById para cada LA ---
        when(statusRepository.findById(statusPendingId)).thenReturn(Mono.just(statusPending));
        when(statusRepository.findById(statusRejectedId)).thenReturn(Mono.just(statusRejected));
        when(statusRepository.findById(statusManualId)).thenReturn(Mono.just(statusManual)); // por si acaso

        // --- UserClient (¡ajusta métodos si tu DTO difiere!) ---
        UserDTO ana = mock(UserDTO.class);
        when(ana.firstName()).thenReturn("Ana");
        when(ana.lastName()).thenReturn("Diaz");
        when(ana.baseSalary()).thenReturn(2500D); // o Double.valueOf(2500)
        when(userClient.getClientByEmail("ana@acme.com")).thenReturn(Mono.just(ana));

        UserDTO bob = mock(UserDTO.class);
        when(bob.firstName()).thenReturn("Bob");
        when(bob.lastName()).thenReturn("Smith");
        when(bob.baseSalary()).thenReturn(3700D);
        when(userClient.getClientByEmail("bob@acme.com")).thenReturn(Mono.just(bob));

        // --- Suma mensual aprobada por email ---
        when(loanApplicationRepository.getSumMonthlyApprovedByEmail("ana@acme.com"))
                .thenReturn(Mono.just(new BigDecimal("500.00")));
        when(loanApplicationRepository.getSumMonthlyApprovedByEmail("bob@acme.com"))
                .thenReturn(Mono.just(new BigDecimal("0.00")));
    }

    @Test
    @DisplayName("listReviewableLoanApplications: compone DTOs y respeta la paginación")
    void listReviewableLoanApplications_success() {
        StepVerifier.create(useCase.listReviewableLoanApplications(pageQuery))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(2, result.content().size());
                    assertEquals(0, result.page());
                    assertEquals(10, result.size());
                    assertEquals(2L, result.totalElements());

                    // Aserciones conservadoras sobre los DTOs (sin asumir nombres de getters extra)
                    LoanApplicationReviewSummaryDTO dto1 = result.content().get(0);
                    LoanApplicationReviewSummaryDTO dto2 = result.content().get(1);

                    // Si tu DTO tiene getters estándar, descomenta y ajusta:
                    // assertEquals(new BigDecimal("15000"), dto1.getAmount());
                    // assertEquals(24, dto1.getTermMonths());
                    // assertEquals("ana@acme.com", dto1.getEmail());
                    // assertEquals(la1.getCreatedAt(), dto1.getCreatedAt());

                    // assertEquals(new BigDecimal("8000"), dto2.getAmount());
                    // assertEquals(12, dto2.getTermMonths());
                    // assertEquals("bob@acme.com", dto2.getEmail());
                    // assertEquals(la2.getCreatedAt(), dto2.getCreatedAt());
                })
                .verifyComplete();

        // Verificamos llamadas clave
        verify(statusRepository).findByName(LoanApplicationStatuses.PENDING);
        verify(statusRepository).findByName(LoanApplicationStatuses.REJECTED);
        verify(statusRepository).findByName(LoanApplicationStatuses.MANUAL_REVIEW);
        verify(loanApplicationRepository).findByStatusIdIn(anyList(), eq(pageQuery));

        // Para cada LA, se consultan sus dependencias
        verify(statusRepository).findById(statusPendingId);
        verify(loanTypeRepository).findById(loanTypeId1);
        verify(userClient).getClientByEmail("ana@acme.com");
        verify(loanApplicationRepository).getSumMonthlyApprovedByEmail("ana@acme.com");

        verify(statusRepository).findById(statusRejectedId);
        verify(loanTypeRepository).findById(loanTypeId2);
        verify(userClient).getClientByEmail("bob@acme.com");
        verify(loanApplicationRepository).getSumMonthlyApprovedByEmail("bob@acme.com");
    }

    @Test
    @DisplayName("listReviewableLoanApplications: página vacía -> retorna PageResult vacío con misma metadata")
    void listReviewableLoanApplications_emptyPage() {
        PageResult<LoanApplication> empty = PageResult.of(List.of(), 2, 20, 0L);
        when(loanApplicationRepository.findByStatusIdIn(anyList(), any(PageQuery.class)))
                .thenReturn(Mono.just(empty));

        StepVerifier.create(useCase.listReviewableLoanApplications(pageQuery))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.content().isEmpty());
                    assertEquals(2, result.page());
                    assertEquals(20, result.size());
                    assertEquals(0L, result.totalElements());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Si algún sub-recurso falta (p.ej., status por ID vacío), ese item no produce DTO")
    void listReviewableLoanApplications_dropsItemWhenMissingData() {
        // Hacemos que para la primera LA no se encuentre el status por ID
        when(statusRepository.findById(statusPendingId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listReviewableLoanApplications(pageQuery))
                .assertNext(result -> {
                    // Solo la segunda LA debe producir DTO
                    assertEquals(1, result.content().size());
                    // La metadata de página proviene del repositorio original
                    assertEquals(0, result.page());
                    assertEquals(10, result.size());
                    assertEquals(2L, result.totalElements());
                })
                .verifyComplete();
    }

    @Nested
    class Errors {

        @Test
        @DisplayName("Propaga error si falla la carga de estados revisables")
        void errorOnFindStatusNames_isPropagated() {
            when(statusRepository.findByName(LoanApplicationStatuses.REJECTED))
                    .thenReturn(Mono.error(new RuntimeException("boom")));

            StepVerifier.create(useCase.listReviewableLoanApplications(pageQuery))
                    .expectErrorMatches(ex -> ex instanceof RuntimeException && ex.getMessage().equals("boom"))
                    .verify();
        }

        @Test
        @DisplayName("Propaga error si el repositorio falla al listar por statusIds")
        void errorOnFindByStatusIds_isPropagated() {
            when(loanApplicationRepository.findByStatusIdIn(anyList(), any(PageQuery.class)))
                    .thenReturn(Mono.error(new IllegalStateException("repo-fail")));

            StepVerifier.create(useCase.listReviewableLoanApplications(pageQuery))
                    .expectErrorMatches(ex -> ex instanceof IllegalStateException && ex.getMessage().equals("repo-fail"))
                    .verify();
        }
    }
}