package co.com.pragma.api.data.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanApplicationDTO(
        BigDecimal amount, Integer termMonths, String email, UUID loanTypeId
) {
}
