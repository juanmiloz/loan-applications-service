package co.com.pragma.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "loan", name = "loan_applications")
public class LoanApplicationDAO {

    @Id
    @Column("application_id")
    private UUID applicationId;

    @Column("amount")
    private BigDecimal amount;

    @Column("term_months")
    private Integer termMonths;

    @Column("email")
    private String email;        // citext -> String

    @Column("status_id")
    private UUID statusId;       // FK -> statuses.status_id

    @Column("loan_type_id")
    private UUID loanTypeId;     // FK -> loan_types.loan_type_id

    @Column("created_at")
    private OffsetDateTime createdAt;

}
