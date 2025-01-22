package Expense.Management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import Expense.Management.model.Expense;
import Expense.Management.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find expenses by user
    List<Expense> findByUser(User user);

    // Find expense by user and expense ID
    Optional<Expense> findByUserAndId(User user, Long id);
}
