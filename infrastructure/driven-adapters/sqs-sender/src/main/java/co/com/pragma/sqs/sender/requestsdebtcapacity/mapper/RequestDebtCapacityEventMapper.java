package co.com.pragma.sqs.sender.requestsdebtcapacity.mapper;

import co.com.pragma.model.loanapplication.dto.response.DebtCapacityAutomaticDTO;
import co.com.pragma.sqs.sender.requestsdebtcapacity.event.RequestDebtCapacityEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestDebtCapacityEventMapper {

    RequestDebtCapacityEvent toRequestDebtCapacityEvent(DebtCapacityAutomaticDTO debtCapacityAutomaticDTO);

}
