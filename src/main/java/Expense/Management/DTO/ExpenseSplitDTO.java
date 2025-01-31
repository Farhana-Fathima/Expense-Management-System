package Expense.Management.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseSplitDTO {
    private Long expenseId;
    private String expenseDescription;
    private LocalDateTime createdAt;
}
