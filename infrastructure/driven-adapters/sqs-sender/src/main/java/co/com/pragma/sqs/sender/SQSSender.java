package co.com.pragma.sqs.sender;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanDecisionEventPublisher;
import co.com.pragma.model.status.Status;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.event.LoanDecisionEvent;
import co.com.pragma.sqs.sender.mapper.LoanDecisionEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements LoanDecisionEventPublisher {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final LoanDecisionEventMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> publish(LoanApplication loanApplication, Status status) {
        return Mono.fromCallable(() -> mapper.toLoanDecisionEvent(loanApplication, status))
                .doOnSubscribe(s -> log.info("Sending decision to SQS. loanId={}, status={}", loanApplication.getApplicationId(), status))
                .map(this::buildDecisionRequest)
                .doOnNext(req -> log.debug("SQS queueUrl={}", req.queueUrl()))
                .flatMap(req -> Mono.fromFuture(client.sendMessage(req)))
                .map(resp -> resp.messageId())
                .doOnSuccess(messageId -> log.info("Message sent to SQS. loanId={}, messageId={}", loanApplication.getApplicationId(), messageId))
                .doOnError(e -> log.error("Error sending message to SQS. loanId={}, cause={}", loanApplication.getApplicationId(), e.toString()));
    }

    private SendMessageRequest buildDecisionRequest(LoanDecisionEvent loanDecisionEvent) {
        String groupId = UUID.randomUUID().toString();
        String messageId = groupId+":"+System.currentTimeMillis();

        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(parseObjectToJson(loanDecisionEvent))
                .messageGroupId(groupId)
                .messageDeduplicationId(messageId)
                .build();
    }

    private String parseObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Error serializando a JSON", e);
        }
    }
}
