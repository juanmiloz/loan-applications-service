package co.com.pragma.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

    private UUID applicationId;
    private BigDecimal amount;
    private Integer termMonths;
    private String email;
    private UUID statusId;
    private UUID loanTypeId;
    private OffsetDateTime createdAt;

}
