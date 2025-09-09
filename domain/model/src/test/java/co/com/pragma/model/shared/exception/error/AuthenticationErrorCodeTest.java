package co.com.pragma.model.shared.exception.error;

import co.com.pragma.model.shared.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class AuthenticationErrorCodeTest {

    @Test
    @DisplayName("Cada constante expone appCode, httpCode y message correctos")
    void metadataByConstant() {
        Map<AuthenticationErrorCode, String> appCodes = Map.of(
                AuthenticationErrorCode.INVALID_TOKEN, "AUTH-001",
                AuthenticationErrorCode.EXPIRED_TOKEN, "AUTH-002",
                AuthenticationErrorCode.MALFORMED_TOKEN, "AUTH-003",
                AuthenticationErrorCode.UNSUPPORTED_TOKEN, "AUTH-004",
                AuthenticationErrorCode.BAD_SIGNATURE, "AUTH-005",
                AuthenticationErrorCode.ILLEGAL_ARGUMENT, "AUTH-006",
                AuthenticationErrorCode.MISSING_TOKEN, "AUTH-007"
        );

        Map<AuthenticationErrorCode, Integer> httpCodes = Map.of(
                AuthenticationErrorCode.INVALID_TOKEN, 401,
                AuthenticationErrorCode.EXPIRED_TOKEN, 401,
                AuthenticationErrorCode.MALFORMED_TOKEN, 400,
                AuthenticationErrorCode.UNSUPPORTED_TOKEN, 400,
                AuthenticationErrorCode.BAD_SIGNATURE, 401,
                AuthenticationErrorCode.ILLEGAL_ARGUMENT, 400,
                AuthenticationErrorCode.MISSING_TOKEN, 401
        );

        Map<AuthenticationErrorCode, String> messages = Map.of(
                AuthenticationErrorCode.INVALID_TOKEN, "Invalid JWT token",
                AuthenticationErrorCode.EXPIRED_TOKEN, "Expired JWT token",
                AuthenticationErrorCode.MALFORMED_TOKEN, "Malformed JWT token",
                AuthenticationErrorCode.UNSUPPORTED_TOKEN, "Unsupported JWT token",
                AuthenticationErrorCode.BAD_SIGNATURE, "Invalid JWT signature",
                AuthenticationErrorCode.ILLEGAL_ARGUMENT, "Illegal argument in JWT processing",
                AuthenticationErrorCode.MISSING_TOKEN, "JWT token was not found"
        );

        for (AuthenticationErrorCode c : AuthenticationErrorCode.values()) {
            assertThat(c.getAppCode()).isEqualTo(appCodes.get(c));
            assertThat(c.getHttpCode()).isEqualTo(httpCodes.get(c));
            assertThat(c.getMessage()).isEqualTo(messages.get(c));
        }
    }

    @Test
    @DisplayName("values() incluye todas las constantes y valueOf() funciona")
    void valuesAndValueOf() {
        AuthenticationErrorCode[] values = AuthenticationErrorCode.values();
        assertThat(values).hasSize(7);
        assertThat(EnumSet.allOf(AuthenticationErrorCode.class))
                .containsExactlyInAnyOrder(values);

        assertThat(AuthenticationErrorCode.valueOf("INVALID_TOKEN"))
                .isSameAs(AuthenticationErrorCode.INVALID_TOKEN);
        assertThat(AuthenticationErrorCode.valueOf("MISSING_TOKEN"))
                .isSameAs(AuthenticationErrorCode.MISSING_TOKEN);
    }

    @Test
    @DisplayName("El enum implementa ErrorCode")
    void implementsErrorCode() {
        for (AuthenticationErrorCode c : AuthenticationErrorCode.values()) {
            assertThat(c).isInstanceOf(ErrorCode.class);
        }
    }

    @Test
    @DisplayName("appCode es único por constante")
    void appCodesAreUnique() {
        Set<String> codes = new HashSet<>();
        for (AuthenticationErrorCode c : AuthenticationErrorCode.values()) {
            assertThat(codes.add(c.getAppCode()))
                    .as("Duplicated appCode: " + c.getAppCode())
                    .isTrue();
        }
    }

}
