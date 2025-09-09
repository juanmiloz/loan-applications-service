package co.com.pragma.api.data.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LoanApplicationReviewSummaryDTO (
        BigDecimal amount,
        Integer termMonths,
        String email,
        String name,
        UUID loanTypeName,
        BigDecimal interestRate,
        UUID statusName,
        Double baseSalary,
        Double monthlyLoanApplicationApproved,
        OffsetDateTime createdAt
) {
}
