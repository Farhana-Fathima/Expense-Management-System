package Expense.Management.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ParticipantPaymentRequest {

    private Long participantId;
    private BigDecimal amount;
}
