package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.dto.response.MetricApprovedDTO;
import reactor.core.publisher.Mono;

public interface MetricApprovedEventPublisher {

    Mono<String> publish(MetricApprovedDTO metricApprovedDTO);

}
