// package Expense.Management.repository;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import Expense.Management.model.Expense;

// public interface ExpenseRepository extends JpaRepository<Expense, Long> {
//     @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId " +
//            "ORDER BY e.date DESC")
//     List<Expense> findAllByGroupId(@Param("groupId") Long groupId);
    
//     @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
//            "ORDER BY e.date DESC")
//     List<Expense> findAllByUserId(@Param("userId") Long userId);
    
//     @Query("SELECT e FROM Expense e " +
//            "WHERE e.group.id = :groupId " +
//            "AND e.date BETWEEN :startDate AND :endDate " +
//            "ORDER BY e.date DESC")
//     List<Expense> findByGroupIdAndDateBetween(
//         @Param("groupId") Long groupId,
//         @Param("startDate") LocalDateTime startDate,
//         @Param("endDate") LocalDateTime endDate
//     );
    
//     @Query("SELECT e FROM Expense e " +
//            "WHERE e.user.id = :userId " +
//            "AND e.date BETWEEN :startDate AND :endDate " +
//            "ORDER BY e.date DESC")
//     List<Expense> findByUserIdAndDateBetween(
//         @Param("userId") Long userId,
//         @Param("startDate") LocalDateTime startDate,
//         @Param("endDate") LocalDateTime endDate
//     );
    
//     @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group.id = :groupId")
//     BigDecimal getTotalExpensesByGroupId(@Param("groupId") Long groupId);
    
//     @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
//     BigDecimal getTotalExpensesByUserId(@Param("userId") Long userId);
// }
