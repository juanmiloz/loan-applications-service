package co.com.pragma.usecase.loanapplicationcrud.util;

import co.com.pragma.model.loanapplication.LoanApplication;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class LoanCreator {

    public static LoanApplication base(UUID loanTypeId) {
        return LoanApplication.builder()
                .applicationId(null)
                .amount(BigDecimal.valueOf(10_000))
                .termMonths(12)
                .email("applicant@mail.com")
                .statusId(null)
                .loanTypeId(loanTypeId)
                .createdAt(null)
                .build();
    }

    public static LoanApplication withCreatedAt(UUID loanTypeId) {
        return base(loanTypeId).toBuilder()
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
    }

    public static LoanApplication nullAmount(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().amount(null).build();
    }

    public static LoanApplication nullTerm(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().termMonths(null).build();
    }

    public static LoanApplication nullEmail(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().email(null).build();
    }

    public static LoanApplication invalidEmail(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().email("not-an-email").build();
    }

    public static LoanApplication zeroTerm(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().termMonths(0).build();
    }

    public static LoanApplication negativeTerm(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().termMonths(-6).build();
    }

    public static LoanApplication amountBelowMin(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().amount(BigDecimal.valueOf(500)).build();
    }

    public static LoanApplication amountAboveMax(UUID loanTypeId) {
        return base(loanTypeId).toBuilder().amount(BigDecimal.valueOf(1_000_000)).build();
    }

}
