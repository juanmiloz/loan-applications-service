package co.com.pragma.model.loanapplication.dto.response;

import java.math.BigDecimal;

public record MetricApprovedDTO(
        Integer additionalLoanApproved,
        BigDecimal additionalAmountApproved
) {
}
