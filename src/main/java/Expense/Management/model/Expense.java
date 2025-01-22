package Expense.Management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double amount;

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

    @Column(name = "expense_date")
    private LocalDateTime expenseDate;
    
    private String category;  // e.g., Food, Travel, Rent

    private String receipt;  // URL or file path to the receipt image or document

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
