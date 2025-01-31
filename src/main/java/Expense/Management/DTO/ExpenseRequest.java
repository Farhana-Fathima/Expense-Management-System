// 


package Expense.Management.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseRequest {

    @NotBlank(message = "Description is required")
    private String description;

    private double amount;

    private LocalDateTime expenseDate;

    @NotBlank(message = "Category is required")
    private String category; // This will be converted to ExpenseCategory in the service

    private String receipt;
}
