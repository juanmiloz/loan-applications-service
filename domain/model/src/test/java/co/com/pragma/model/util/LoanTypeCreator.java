package co.com.pragma.model.util;

import co.com.pragma.model.loantype.LoanType;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanTypeCreator {

    public static LoanType createValidLoanType() {
        return new LoanType(
                UUID.randomUUID(),
                "Personal Loan",
                new BigDecimal("1000"),
                new BigDecimal("50000"),
                new BigDecimal("0.025"),
                true
        );
    }

    public static LoanType createWithNegativeMinAmount() {
        return new LoanType(
                UUID.randomUUID(),
                "Invalid Loan",
                new BigDecimal("-1000"),
                new BigDecimal("5000"),
                new BigDecimal("0.02"),
                false
        );
    }

    public static LoanType createWithMaxLessThanMin() {
        return new LoanType(
                UUID.randomUUID(),
                "Broken Loan",
                new BigDecimal("10000"),
                new BigDecimal("5000"),
                new BigDecimal("0.03"),
                false
        );
    }

    public static LoanType createWithZeroInterestRate() {
        return new LoanType(
                UUID.randomUUID(),
                "Zero Rate Loan",
                new BigDecimal("1000"),
                new BigDecimal("20000"),
                BigDecimal.ZERO,
                false
        );
    }

    public static LoanType createWithHighInterestRate() {
        return new LoanType(
                UUID.randomUUID(),
                "Very Expensive Loan",
                new BigDecimal("1000"),
                new BigDecimal("20000"),
                new BigDecimal("1.50"), // 150%
                false
        );
    }

}
