package Expense.Management.DTO;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseResponse {

    private Long id;
    private Double amount;
    private String description;
    private String category;
    private LocalDateTime expenseDate;
    private String receipt; // Could store a URL or path to the receipt
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
