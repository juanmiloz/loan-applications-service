package co.com.pragma.model.loanapplication.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LoanApplicationErrorCodeTest {

    @Test
    @DisplayName("REQUIRED_AMOUNT debe exponer metadatos correctos")
    void requiredAmount_metadata() {
        LoanApplicationErrorCode code = LoanApplicationErrorCode.REQUIRED_AMOUNT;

        assertThat(code.getAppCode()).isEqualTo("LAP_001");
        assertThat(code.getHttpCode()).isEqualTo(400);
        assertThat(code.getMessage()).isEqualTo("Amount is required for completing the loan application.");
    }

    @Test
    @DisplayName("INVALID_AMOUNT_RANGE debe contener placeholders")
    void invalidAmountRange_metadata() {
        LoanApplicationErrorCode code = LoanApplicationErrorCode.INVALID_AMOUNT_RANGE;

        assertThat(code.getAppCode()).isEqualTo("LAP_004");
        assertThat(code.getHttpCode()).isEqualTo(400);
        assertThat(code.getMessage()).contains("{0}").contains("{1}").contains("{2}");
    }

    @Test
    @DisplayName("values() y valueOf() funcionan")
    void valuesAndValueOf() {
        LoanApplicationErrorCode[] values = LoanApplicationErrorCode.values();
        assertThat(values).isNotEmpty();
        assertThat(LoanApplicationErrorCode.valueOf("REQUIRED_EMAIL"))
                .isEqualTo(LoanApplicationErrorCode.REQUIRED_EMAIL);
    }

}