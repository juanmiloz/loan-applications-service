package co.com.pragma.sqs.sender.metricapprovedsqs.event;

import java.math.BigDecimal;

public record MetricApprovedEvent(
        Integer additionalLoanApproved,
        BigDecimal additionalAmountApproved
) {
}
