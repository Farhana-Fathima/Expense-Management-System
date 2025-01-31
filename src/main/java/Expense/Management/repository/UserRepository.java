package Expense.Management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Expense.Management.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    

      // New method to fetch a user along with their expenses
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.expenses WHERE u.id = :userId")
    Optional<User> findByIdWithExpenses(@Param("userId") Long userId);
}
