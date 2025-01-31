package Expense.Management.repository;

import Expense.Management.model.Group;
import Expense.Management.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByGroup(Group group);
}
