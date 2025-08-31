package co.com.pragma.api.error;

import co.com.pragma.model.shared.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getHttpCode());
        return ResponseEntity.status(status).body(Map.of(
                "httpCode", ex.getHttpCode(),
                "message", ex.getMessage()
        ));
    }

}
