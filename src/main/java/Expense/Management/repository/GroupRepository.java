// package Expense.Management.repository;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import Expense.Management.model.Group;

// public interface GroupRepository extends JpaRepository<Group, Long> {
//     @Query("SELECT g FROM Group g WHERE g.admin.id = :userId")
//     List<Group> findAllByAdminId(@Param("userId") Long userId);
    
//     @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
//     List<Group> findAllByMemberId(@Param("userId") Long userId);
    
//     @Query("SELECT g FROM Group g " +
//            "LEFT JOIN FETCH g.members " +
//            "LEFT JOIN FETCH g.expenses " +
//            "WHERE g.id = :groupId")
//     Optional<Group> findByIdWithDetails(@Param("groupId") Long groupId);
    
//     @Query("SELECT COUNT(m) FROM Group g JOIN g.members m WHERE g.id = :groupId")
//     int getMemberCount(@Param("groupId") Long groupId);
    
//     boolean existsByNameAndAdminId(String name, Long adminId);
// }
