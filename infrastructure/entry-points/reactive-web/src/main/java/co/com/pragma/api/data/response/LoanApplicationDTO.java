package co.com.pragma.api.data.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LoanApplicationDTO(
         UUID applicationId,
         BigDecimal amount,
         Integer termMonths,
         String email,
         UUID statusId,
         UUID loanTypeId,
         OffsetDateTime createdAt
) {
}
