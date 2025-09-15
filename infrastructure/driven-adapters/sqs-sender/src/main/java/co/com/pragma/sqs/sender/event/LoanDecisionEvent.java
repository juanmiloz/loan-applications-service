package co.com.pragma.sqs.sender.event;

import java.util.UUID;

public record LoanDecisionEvent(
        UUID applicationId,
        String statusName,
        String email
) {
}
