package co.com.pragma.sqs.listener.dto;

import java.math.BigDecimal;

public record LoanDecisionMessage(
        String traceId,
        String loanApplicationId,
        Metrics metrics,
        String decision
) {

    public static record Metrics(
            BigDecimal maxDebtCapacity,
            BigDecimal currentMonthlyDebt,
            BigDecimal availableCapacity,
            BigDecimal newLoanInstallment
    ) {}

}
