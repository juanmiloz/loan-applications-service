package co.com.pragma.model.shared.exception;

import co.com.pragma.model.shared.ErrorCode;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }

    public String getAppCode() { return errorCode.getAppCode(); }
    public int getHttpCode()   { return errorCode.getHttpCode(); }
    public String getMessageTemplate() { return errorCode.getMessage(); }
}
