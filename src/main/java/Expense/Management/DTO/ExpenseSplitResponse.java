package Expense.Management.DTO;

import Expense.Management.model.ExpenseCategory;
import Expense.Management.model.ExpenseSplit;
import Expense.Management.model.ParticipantShare;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ExpenseSplitResponse {
    private Long id;
    private String title;
    private BigDecimal amount;
    private ExpenseCategory category;
    private Long groupId;
    private String creator;
    private List<ParticipantShareResponse> participantShares;
    private LocalDateTime createdAt;

    public static ExpenseSplitResponse fromEntity(ExpenseSplit expenseSplit) {
        return ExpenseSplitResponse.builder()
                .id(expenseSplit.getId())
                .title(expenseSplit.getTitle())
                .amount(expenseSplit.getAmount())
                .category(expenseSplit.getCategory())
                .groupId(expenseSplit.getGroup().getId())
                .creator(expenseSplit.getCreator().getUsername())
                .participantShares(expenseSplit.getParticipantShares().stream()
                        .map(ParticipantShareResponse::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(expenseSplit.getCreatedAt())
                .build();
    }

    @Data
    @Builder
    public static class ParticipantShareResponse {
        private Long userId;
        private String userName;
        private BigDecimal amount;
        private BigDecimal percentage;
        private boolean isPaid;
        private LocalDateTime paidAt;

        // FIX: Add this missing method
        public static ParticipantShareResponse fromEntity(ParticipantShare participantShare) {
            return ParticipantShareResponse.builder()
                    .userId(participantShare.getParticipant().getId())
                    .userName(participantShare.getParticipant().getUsername())
                    .amount(participantShare.getAmount())
                    .percentage(participantShare.getPercentage())
                    .isPaid(participantShare.isPaid())
                    .paidAt(participantShare.getPaidAt())
                    .build();
        }
    }
}
