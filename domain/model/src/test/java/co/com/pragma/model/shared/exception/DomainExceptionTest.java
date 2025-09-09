package co.com.pragma.model.shared.exception;

import co.com.pragma.model.loanapplication.error.LoanApplicationErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DomainExceptionTest {

    @Test
    @DisplayName("DomainException should expose ErrorCode metadata and detail message")
    void domainException_exposesMetadata() {
        LoanApplicationErrorCode code = LoanApplicationErrorCode.INVALID_TERM_MONTHS;
        String detail = "Term months must be greater than 0, but was -5.";

        DomainException ex = new DomainException(code, detail);

        assertThat(ex.getErrorCode()).isEqualTo(code);
        assertThat(ex.getAppCode()).isEqualTo("LAP_005");
        assertThat(ex.getHttpCode()).isEqualTo(400);
        assertThat(ex.getMessageTemplate()).isEqualTo("Term months must be greater than 0, but was {0}.");

        assertThat(ex.getMessage()).isEqualTo(detail);
    }

    @Test
    @DisplayName("DomainException should allow different error codes")
    void domainException_withDifferentCodes() {
        DomainException ex = new DomainException(
                LoanApplicationErrorCode.REQUIRED_EMAIL,
                "Email is required for completing the loan application."
        );

        assertThat(ex.getAppCode()).isEqualTo("LAP_003");
        assertThat(ex.getHttpCode()).isEqualTo(400);
        assertThat(ex.getMessageTemplate())
                .isEqualTo("Email is required for completing the loan application.");
        assertThat(ex.getMessage())
                .isEqualTo("Email is required for completing the loan application.");
    }

}