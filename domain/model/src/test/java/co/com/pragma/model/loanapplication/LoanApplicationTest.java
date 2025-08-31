package co.com.pragma.model.loanapplication;

import co.com.pragma.model.util.LoanApplicationCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LoanApplicationTest {

    @Test
    @DisplayName("createValidLoanApplication: campos mínimos válidos")
    void validLoanApplication() {
        LoanApplication la = LoanApplicationCreator.createValidLoanApplication();

        assertThat(la.getApplicationId()).isNull(); // se genera en persistencia
        assertThat(la.getAmount()).isNotNull();
        assertThat(la.getAmount().compareTo(BigDecimal.ZERO)).isPositive();
        assertThat(la.getTermMonths()).isGreaterThan(0);
        assertThat(la.getEmail()).contains("@");
        assertThat(la.getStatusId()).isNotNull();
        assertThat(la.getLoanTypeId()).isNotNull();
        assertThat(la.getCreatedAt()).isNotNull();
        assertThat(la.getCreatedAt()).isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    @DisplayName("createWithBlankEmail: email en blanco")
    void blankEmail() {
        LoanApplication la = LoanApplicationCreator.createWithBlankEmail();
        assertThat(la.getEmail()).isBlank();
    }

    @Test
    @DisplayName("createWithInvalidEmail: email inválido")
    void invalidEmail() {
        LoanApplication la = LoanApplicationCreator.createWithInvalidEmail();
        assertThat(la.getEmail()).doesNotContain("@");
    }

    @Test
    @DisplayName("createWithNegativeAmount: monto negativo")
    void negativeAmount() {
        LoanApplication la = LoanApplicationCreator.createWithNegativeAmount();
        assertThat(la.getAmount().compareTo(BigDecimal.ZERO)).isNegative();
    }

    @Test
    @DisplayName("createWithTooHighAmount: monto inusualmente alto")
    void tooHighAmount() {
        LoanApplication la = LoanApplicationCreator.createWithTooHighAmount();
        assertThat(la.getAmount().compareTo(new BigDecimal("1000000"))).isPositive();
    }

    @Test
    @DisplayName("createWithZeroTerm: plazo igual a cero")
    void zeroTerm() {
        LoanApplication la = LoanApplicationCreator.createWithZeroTerm();
        assertThat(la.getTermMonths()).isZero();
    }

}