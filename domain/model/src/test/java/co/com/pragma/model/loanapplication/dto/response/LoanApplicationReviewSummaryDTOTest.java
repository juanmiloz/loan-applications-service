package co.com.pragma.model.loanapplication.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoanApplicationReviewSummaryDTOTest {

    @Test
    @DisplayName("LoanApplicationReviewSummaryDTO: getters y valores")
    void summaryBasics() {
        OffsetDateTime now = OffsetDateTime.now();
        LoanApplicationReviewSummaryDTO dto = new LoanApplicationReviewSummaryDTO(
                new BigDecimal("15000.50"),
                24,
                "applicant@mail.com",
                "Juan Pérez",
                "Personal",
                new BigDecimal("1.45"),
                "APPROVED",
                3_800_000D,
                650_000D,
                now
        );

        assertThat(dto.amount()).isEqualByComparingTo("15000.50");
        assertThat(dto.termMonths()).isEqualTo(24);
        assertThat(dto.email()).isEqualTo("applicant@mail.com");
        assertThat(dto.name()).isEqualTo("Juan Pérez");
        assertThat(dto.loanTypeName()).isEqualTo("Personal");
        assertThat(dto.interestRate()).isEqualByComparingTo("1.45");
        assertThat(dto.statusName()).isEqualTo("APPROVED");
        assertThat(dto.baseSalary()).isEqualTo(3_800_000D);
        assertThat(dto.monthlyLoanApplicationApproved()).isEqualTo(650_000D);
        assertThat(dto.createdAt()).isEqualTo(now);
    }

}