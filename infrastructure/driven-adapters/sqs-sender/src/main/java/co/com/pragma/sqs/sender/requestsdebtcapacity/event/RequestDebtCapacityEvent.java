package co.com.pragma.sqs.sender.requestsdebtcapacity.event;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestDebtCapacityEvent(
        UUID loanApplicationId,
        String email,
        BigDecimal baseSalary,
        BigDecimal currentMonthlyDebt,
        BigDecimal monthlyInterest,
        BigDecimal monthsTerm,
        BigDecimal amount
) {
}
