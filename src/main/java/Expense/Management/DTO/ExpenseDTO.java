package Expense.Management.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;
    private String description;
    private double amount;
    private LocalDateTime createdAt;
}

