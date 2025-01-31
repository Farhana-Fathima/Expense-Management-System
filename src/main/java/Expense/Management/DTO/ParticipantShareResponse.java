package Expense.Management.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ParticipantShareResponse {

    private String participant;
    private BigDecimal amount;
    private BigDecimal percentage;
    private boolean isPaid;
}
