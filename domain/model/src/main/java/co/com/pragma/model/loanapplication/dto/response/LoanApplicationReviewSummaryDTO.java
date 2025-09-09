package co.com.pragma.model.loanapplication.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LoanApplicationReviewSummaryDTO(
        BigDecimal amount,
        Integer termMonths,
        String email,
        String name,
        String loanTypeName,
        BigDecimal interestRate,
        String statusName,
        Double baseSalary,
        Double monthlyLoanApplicationApproved,
        OffsetDateTime createdAt
) {
}
