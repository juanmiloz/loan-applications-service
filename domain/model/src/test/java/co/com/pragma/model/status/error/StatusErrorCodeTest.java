package co.com.pragma.model.status.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StatusErrorCodeTest {

    @Test
    @DisplayName("STATUS_NOT_FOUND_BY_NAME debe exponer metadatos correctos")
    void statusNotFound_metadata() {
        StatusErrorCode code = StatusErrorCode.STATUS_NOT_FOUND_BY_NAME;

        assertThat(code.getAppCode()).isEqualTo("STS_001");
        assertThat(code.getHttpCode()).isEqualTo(404);
        assertThat(code.getMessage()).isEqualTo("Status not found with name {0}");
    }

    @Test
    @DisplayName("values() y valueOf() funcionan")
    void valuesAndValueOf() {
        assertThat(StatusErrorCode.valueOf("STATUS_NOT_FOUND_BY_NAME"))
                .isEqualTo(StatusErrorCode.STATUS_NOT_FOUND_BY_NAME);
    }

}