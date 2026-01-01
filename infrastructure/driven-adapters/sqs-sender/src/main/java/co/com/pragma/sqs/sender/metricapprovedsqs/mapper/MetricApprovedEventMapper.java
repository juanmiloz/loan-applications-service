package co.com.pragma.sqs.sender.metricapprovedsqs.mapper;

import co.com.pragma.model.loanapplication.dto.response.MetricApprovedDTO;
import co.com.pragma.sqs.sender.metricapprovedsqs.event.MetricApprovedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MetricApprovedEventMapper {

    MetricApprovedEvent toMetricApprovedEvent(MetricApprovedDTO metricApprovedDTO);

}
