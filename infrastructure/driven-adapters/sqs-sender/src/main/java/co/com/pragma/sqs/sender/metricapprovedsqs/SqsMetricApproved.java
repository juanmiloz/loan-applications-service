package co.com.pragma.sqs.sender.metricapprovedsqs;

import co.com.pragma.model.loanapplication.dto.response.MetricApprovedDTO;
import co.com.pragma.model.loanapplication.gateways.MetricApprovedEventPublisher;
import co.com.pragma.sqs.sender.config.SQSProperties;
import co.com.pragma.sqs.sender.metricapprovedsqs.event.MetricApprovedEvent;
import co.com.pragma.sqs.sender.metricapprovedsqs.mapper.MetricApprovedEventMapper;
import co.com.pragma.sqs.sender.shared.helper.JsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


@Service
@Log4j2
@RequiredArgsConstructor
public class SqsMetricApproved implements MetricApprovedEventPublisher {

    private final SQSProperties properties;
    private final SqsAsyncClient client;
    private final JsonHelper jsonHelper;
    private final MetricApprovedEventMapper mapper;

    private final static String SQS_PATH = "/metrics-approved";

    @Override
    public Mono<String> publish(MetricApprovedDTO metricApprovedDTO) {
        return Mono.fromCallable(() -> mapper.toMetricApprovedEvent(metricApprovedDTO))
                .doOnSubscribe(s -> log.info("Sending metrics to SQS."))
                .map(this::buildDecisionRequest)
                .doOnNext(req -> log.debug("SQS queueUrl={}", req.queueUrl()))
                .flatMap(req -> Mono.fromFuture(client.sendMessage(req)))
                .map(SendMessageResponse::messageId)
                .doOnSuccess(messageId -> log.info("Message sent to SQS."))
                .doOnError(e -> log.error("Error sending message to SQS. cause={}", e.toString()));
    }

    private SendMessageRequest buildDecisionRequest(MetricApprovedEvent metricApprovedEvent) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl()+ SQS_PATH)
                .messageBody(jsonHelper.toJson(metricApprovedEvent))
                .build();
    }


}
