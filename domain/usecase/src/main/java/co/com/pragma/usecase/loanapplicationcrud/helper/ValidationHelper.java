package co.com.pragma.usecase.loanapplicationcrud.helper;

import co.com.pragma.model.shared.ErrorCode;
import co.com.pragma.model.shared.exception.DomainExceptionFactory;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.regex.Pattern;

@UtilityClass
public final class ValidationHelper {

    public static void requireNotNull(Object value, ErrorCode errorCode, Object... args) {
        if (Objects.isNull(value)) {
            throw DomainExceptionFactory.exceptionOf(errorCode, args);
        }
    }

    public static void requireNotBlank(String value, ErrorCode errorCode, Object... args) {
        if (value == null || value.trim().isBlank()) {
            throw DomainExceptionFactory.exceptionOf(errorCode, args);
        }
    }

    public static void requirePositive(Integer value, ErrorCode errorCode) {
        if (value == null || value <= 0) {
            throw DomainExceptionFactory.exceptionOf(errorCode, value);
        }
    }

    public static void requiredRegex(String value, Pattern pattern, ErrorCode errorCode, Object... args) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw DomainExceptionFactory.exceptionOf(errorCode, value);
        }
    }

}
