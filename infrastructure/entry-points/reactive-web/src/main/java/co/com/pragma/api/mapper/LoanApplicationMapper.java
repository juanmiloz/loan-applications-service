package co.com.pragma.api.mapper;

import co.com.pragma.api.data.request.CreateLoanApplicationDTO;
import co.com.pragma.api.data.response.LoanApplicationDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    LoanApplication toEntity(CreateLoanApplicationDTO dto);

    LoanApplicationDTO toDTO(LoanApplication entity);

}
