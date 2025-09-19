package co.com.pragma.sqs.sender.loandecisionsqs.event;

import java.util.UUID;

public record LoanDecisionEvent(
        UUID applicationId,
        String statusName,
        String email
) {
}
