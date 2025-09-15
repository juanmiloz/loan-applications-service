package co.com.pragma.sqs.sender.mapper;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.status.Status;
import co.com.pragma.sqs.sender.event.LoanDecisionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanDecisionEventMapper {

    @Mapping(source = "status.name", target = "statusName")
    LoanDecisionEvent toLoanDecisionEvent(LoanApplication loanApplication, Status status);

}
