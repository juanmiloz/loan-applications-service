package co.com.pragma.model.loanapplication.error;

import co.com.pragma.model.shared.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanApplicationErrorCode implements ErrorCode {

    REQUIRED_AMOUNT("LAP_001", 400, "Amount is required for completing the loan application."),
    REQUIRED_TERM_MONTHS("LAP_002", 400, "Term months is required for completing the loan application."),
    REQUIRED_EMAIL("LAP_003", 400, "Email is required for completing the loan application."),
    INVALID_AMOUNT_RANGE("LAP_004", 400, "Amount {0} is out of allowed range. It must be between {1} and {2}."),
    INVALID_TERM_MONTHS("LAP_005", 400, "Term months must be greater than 0, but was {0}."),
    INVALID_EMAIL_FORMAT("LAP_006", 400, "Email ''{0}'' does not have a valid format.");

    private final String appCode;
    private final int httpCode;
    private final String message;

}
