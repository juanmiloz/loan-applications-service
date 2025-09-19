package co.com.pragma.sqs.sender.requestsdebtcapacity;

import co.com.pragma.model.loanapplication.dto.response.DebtCapacityAutomaticDTO;
import co.com.pragma.model.loanapplication.gateways.RequestDebtCapacityEventPublisher;
import co.com.pragma.sqs.sender.config.SQSProperties;
import co.com.pragma.sqs.sender.requestsdebtcapacity.event.RequestDebtCapacityEvent;
import co.com.pragma.sqs.sender.requestsdebtcapacity.mapper.RequestDebtCapacityEventMapper;
import co.com.pragma.sqs.sender.shared.helper.JsonHelper;
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
public class SqsRequestDebtCapacity implements RequestDebtCapacityEventPublisher {

    private final SQSProperties properties;
    private final SqsAsyncClient client;
    private final RequestDebtCapacityEventMapper mapper;
    private final JsonHelper jsonHelper;

    @Override
    public Mono<String> publish(DebtCapacityAutomaticDTO debtCapacityAutomaticDTO) {
        return Mono.fromCallable(() -> mapper.toRequestDebtCapacityEvent(debtCapacityAutomaticDTO))
                .doOnSubscribe(s -> log.info("Sending request debt capacity to SQS. loanId={}",debtCapacityAutomaticDTO.loanApplicationId()))
                .map(this::buildDecisionRequest)
                .doOnNext(req -> log.debug("SQS queueUrl={}", req.queueUrl()))
                .flatMap(req -> Mono.fromFuture(client.sendMessage(req)))
                .map(resp -> resp.messageId())
                .doOnSuccess(messageId -> log.info("Message sent to SQS. loanId={}, messageId={}", debtCapacityAutomaticDTO.loanApplicationId(), messageId))
                .doOnError(e -> log.error("Error sending message to SQS. loanId={}, cause={}", debtCapacityAutomaticDTO.loanApplicationId(), e.toString()));
    }


    private SendMessageRequest buildDecisionRequest(RequestDebtCapacityEvent requestDebtCapacityEvent) {
        String groupId = UUID.randomUUID().toString();
        String messageId = groupId + ":" + System.currentTimeMillis();
        log.debug("Message for sqs queue: {}", requestDebtCapacityEvent);

        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl()+"/requests-debt-capacity-automatic.fifo")
                .messageBody(jsonHelper.toJson(requestDebtCapacityEvent))
                .messageGroupId(groupId)
                .messageDeduplicationId(messageId)
                .build();
    }
}
