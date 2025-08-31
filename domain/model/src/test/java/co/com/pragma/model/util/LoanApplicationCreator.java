package co.com.pragma.model.util;

import co.com.pragma.model.loanapplication.LoanApplication;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class LoanApplicationCreator {

    public static LoanApplication createValidLoanApplication() {
        return new LoanApplication(
                null,
                new BigDecimal("15000"),
                24,
                "applicant@mail.com",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    public static LoanApplication createWithBlankEmail() {
        return new LoanApplication(
                null,
                new BigDecimal("10000"),
                12,
                "   ",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    public static LoanApplication createWithInvalidEmail() {
        return new LoanApplication(
                null,
                new BigDecimal("10000"),
                12,
                "not-an-email",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    public static LoanApplication createWithNegativeAmount() {
        return new LoanApplication(
                null,
                new BigDecimal("-5000"),
                12,
                "test@mail.com",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    public static LoanApplication createWithTooHighAmount() {
        return new LoanApplication(
                null,
                new BigDecimal("100000000"),
                36,
                "test@mail.com",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    public static LoanApplication createWithZeroTerm() {
        return new LoanApplication(
                null,
                new BigDecimal("5000"),
                0,
                "test@mail.com",
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

}
