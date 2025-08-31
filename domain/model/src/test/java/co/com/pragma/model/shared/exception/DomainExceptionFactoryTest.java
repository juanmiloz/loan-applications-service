package co.com.pragma.model.shared.exception;

import co.com.pragma.model.loanapplication.error.LoanApplicationErrorCode;
import co.com.pragma.model.loantype.error.LoanTypeErrorCode;
import co.com.pragma.model.status.error.StatusErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DomainExceptionFactoryTest {

    @Test
    @DisplayName("exceptionOf should format message with arguments")
    void exceptionOf_formatsMessage() {
        DomainException ex = DomainExceptionFactory.exceptionOf(
                LoanApplicationErrorCode.INVALID_AMOUNT_RANGE,
                "1000", "500", "800"
        );

        assertThat(ex.getAppCode()).isEqualTo("LAP_004");
        assertThat(ex.getHttpCode()).isEqualTo(400);
        assertThat(ex.getMessage()).contains("Amount 1000 is out of allowed range. It must be between 500 and 800.");
        assertThat(ex.getMessageTemplate()).isEqualTo("Amount {0} is out of allowed range. It must be between {1} and {2}.");
        assertThat(ex.getErrorCode()).isEqualTo(LoanApplicationErrorCode.INVALID_AMOUNT_RANGE);
    }

    @Test
    @DisplayName("StatusErrorCode should expose correct metadata")
    void statusErrorCode_metadata() {
        DomainException ex = DomainExceptionFactory.exceptionOf(
                StatusErrorCode.STATUS_NOT_FOUND_BY_NAME,
                "ACTIVE"
        );

        assertThat(ex.getAppCode()).isEqualTo("STS_001");
        assertThat(ex.getHttpCode()).isEqualTo(404);
        assertThat(ex.getMessage()).contains("Status not found with name ACTIVE");
        assertThat(ex.getMessageTemplate()).isEqualTo("Status not found with name {0}");
        assertThat(ex.getErrorCode()).isEqualTo(StatusErrorCode.STATUS_NOT_FOUND_BY_NAME);
    }

    @Test
    @DisplayName("LoanTypeErrorCode should expose correct metadata")
    void loanTypeErrorCode_metadata() {
        DomainException ex = DomainExceptionFactory.exceptionOf(
                LoanTypeErrorCode.LOAN_TYPE_NOT_FOUND,
                "1234-uuid"
        );

        assertThat(ex.getAppCode()).isEqualTo("LNT_001");
        assertThat(ex.getHttpCode()).isEqualTo(404);
        assertThat(ex.getMessage()).contains("Loan type not found with id 1234-uuid");
        assertThat(ex.getMessageTemplate()).isEqualTo("Loan type not found with id {0}");
        assertThat(ex.getErrorCode()).isEqualTo(LoanTypeErrorCode.LOAN_TYPE_NOT_FOUND);
    }

    @Test
    @DisplayName("LoanApplicationErrorCode REQUIRED_EMAIL should produce static message")
    void loanApplicationErrorCode_requiredEmail() {
        DomainException ex = DomainExceptionFactory.exceptionOf(
                LoanApplicationErrorCode.REQUIRED_EMAIL
        );

        assertThat(ex.getAppCode()).isEqualTo("LAP_003");
        assertThat(ex.getHttpCode()).isEqualTo(400);
        assertThat(ex.getMessage()).isEqualTo("Email is required for completing the loan application.");
        assertThat(ex.getMessageTemplate()).isEqualTo("Email is required for completing the loan application.");
    }

}