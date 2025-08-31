package co.com.pragma.model.loantype.error;

import co.com.pragma.model.shared.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanTypeErrorCode implements ErrorCode {

    LOAN_TYPE_NOT_FOUND("LNT_001", 404, "Loan type not found with id {0}");

    private final String appCode;
    private final int httpCode;
    private final String message;
}
