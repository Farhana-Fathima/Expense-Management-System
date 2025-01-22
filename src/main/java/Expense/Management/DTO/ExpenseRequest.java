package Expense.Management.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExpenseRequest {

    private Double amount;
    private String description;
    private LocalDateTime expenseDate;
    private String category;
    private String receipt;  // URL or file path of the receipt
}
