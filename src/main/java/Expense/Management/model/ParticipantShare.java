package Expense.Management.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participant_shares")
public class ParticipantShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExpenseSplit expenseSplit;

    @ManyToOne
    private User participant;

    private BigDecimal amount;
    private BigDecimal percentage;

    @Column(nullable = false)
    private boolean isPaid;

    private LocalDateTime paidAt;
}