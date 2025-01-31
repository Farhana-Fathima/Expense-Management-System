package Expense.Management.repository;

import Expense.Management.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    // Find all expenses within a group
    List<ExpenseSplit> findByGroupId(Long groupId);

    // Find expenses created by a specific user within a group
    List<ExpenseSplit> findByGroupIdAndCreatorId(Long groupId, Long creatorId);
}
