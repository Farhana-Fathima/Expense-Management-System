package Expense.Management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import Expense.Management.model.Group;
import Expense.Management.model.User;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findById(Long id);

    // Add additional query methods as needed, e.g., finding groups by admin

    // Find a group by its name (case-insensitive)
    Optional<Group> findByNameIgnoreCase(String name);

    // Find all groups for a specific user as a participant
    List<Group> findByParticipantsContaining(User user);

    // Find all groups where a specific user is an admin
    List<Group> findByAdmin(User admin);

    // Find a group by its ID and admin username
    Optional<Group> findByIdAndAdmin_Username(Long groupId, String adminUsername);

}
