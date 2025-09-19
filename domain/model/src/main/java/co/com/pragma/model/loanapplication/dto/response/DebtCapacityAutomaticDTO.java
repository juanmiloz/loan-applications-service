package co.com.pragma.model.loanapplication.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtCapacityAutomaticDTO(
        UUID loanApplicationId,
        String email,
        BigDecimal baseSalary,
        BigDecimal currentMonthlyDebt,
        BigDecimal monthlyInterest,
        BigDecimal monthsTerm,
        BigDecimal amount
) {
}
