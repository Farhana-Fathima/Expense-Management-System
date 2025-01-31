package Expense.Management.repository;

import Expense.Management.model.ParticipantShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantShareRepository extends JpaRepository<ParticipantShare, Long> {

    // Find all participant shares for a given expense split
    List<ParticipantShare> findByExpenseSplitId(Long expenseSplitId);

    // Find a participant's share in a specific expense split using username instead of ID
    Optional<ParticipantShare> findByExpenseSplitIdAndParticipant_Username(Long expenseSplitId, String username);

    // Find all unpaid participant shares for an expense split
    List<ParticipantShare> findByExpenseSplitIdAndIsPaidFalse(Long expenseSplitId);
}
