package co.com.pragma.model.loantype.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoanTypeErrorCodeTest {
    @Test
    @DisplayName("LOAN_TYPE_NOT_FOUND debe exponer metadatos correctos")
    void loanTypeNotFound_metadata() {
        LoanTypeErrorCode code = LoanTypeErrorCode.LOAN_TYPE_NOT_FOUND;

        assertThat(code.getAppCode()).isEqualTo("LNT_001");
        assertThat(code.getHttpCode()).isEqualTo(404);
        assertThat(code.getMessage()).isEqualTo("Loan type not found with id {0}");
    }

    @Test
    @DisplayName("values() y valueOf() funcionan")
    void valuesAndValueOf() {
        assertThat(LoanTypeErrorCode.valueOf("LOAN_TYPE_NOT_FOUND"))
                .isEqualTo(LoanTypeErrorCode.LOAN_TYPE_NOT_FOUND);
    }
}