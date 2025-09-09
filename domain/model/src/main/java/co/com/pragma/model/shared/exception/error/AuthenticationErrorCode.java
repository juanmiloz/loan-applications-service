package co.com.pragma.model.shared.exception.error;

import co.com.pragma.model.shared.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {

    INVALID_TOKEN("AUTH-001", 401, "Invalid JWT token"),
    EXPIRED_TOKEN("AUTH-002", 401, "Expired JWT token"),
    MALFORMED_TOKEN("AUTH-003", 400, "Malformed JWT token"),
    UNSUPPORTED_TOKEN("AUTH-004", 400, "Unsupported JWT token"),
    BAD_SIGNATURE("AUTH-005", 401, "Invalid JWT signature"),
    ILLEGAL_ARGUMENT("AUTH-006", 400, "Illegal argument in JWT processing"),
    MISSING_TOKEN("AUTH-007", 401, "JWT token was not found");


    private final String appCode;
    private final int httpCode;
    private final String message;
}
