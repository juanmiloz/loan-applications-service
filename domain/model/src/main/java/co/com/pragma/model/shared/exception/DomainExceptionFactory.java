package co.com.pragma.model.shared.exception;

import co.com.pragma.model.shared.ErrorCode;
import lombok.experimental.UtilityClass;

import java.text.MessageFormat;

@UtilityClass
public class DomainExceptionFactory {

    public static DomainException exceptionOf(ErrorCode code, Object... args ) {
        String message = MessageFormat.format( code.getMessage(), args );
        return new DomainException(code, message);
    }

}
