package co.com.pragma.model.status.error;

import co.com.pragma.model.shared.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusErrorCode implements ErrorCode {

    STATUS_NOT_FOUND_BY_NAME("STS_001", 404, "Status not found with name {0}"),
    STATUS_NOT_FOUND("STS_002", 404, "Status not found with id {0}");

    private final String appCode;
    private final int httpCode;
    private final String message;

}
