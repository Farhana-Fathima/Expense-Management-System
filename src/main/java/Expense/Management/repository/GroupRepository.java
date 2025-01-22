package Expense.Management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import Expense.Management.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findById(Long id);

    // Add additional query methods as needed, e.g., finding groups by admin
}
